package com.xinlian.common.utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class HttpClientUtil {
	protected final static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

	private static PoolingHttpClientConnectionManager cm;

	private static HttpRequestRetryHandler httpRequestRetryHandler;

	private static RequestConfig requestConfig;

	private static CloseableHttpClient httpClient;
	
	public static final String UTF_8 = "utf-8";
	/**
	 * 最大连接数
 	 */
	private static final int HTTP_MAX_TOTAL = 200;
	/**
	 * 默认，每个路由基础上的连接不超过2个，总连接数不能超过20
	 */
	private static final int HTTP_DEFAULT_MAX_PERROUTE = 20;
	// 链接超期时间
	private static final int HTTP_TIME_OUT = 3000;
	// 已经重试次数，超过重拾次数就放弃
	private static final int HTTP_EXECUTION_COUNT = 3;
	//空闲永久连接检查间隔
	private static final int IN_ACTIVITY_TIME = 60 * 1000;

	static {
		//http协议
		ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
		//支持https协议
		LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();
		//
		Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
				.register("http", plainsf).register("https", sslsf).build();
		cm = new PoolingHttpClientConnectionManager(registry);
		// 最大连接数
		cm.setMaxTotal(HTTP_MAX_TOTAL);
		//每个路由基础上的连接不超过2个，总连接数不能超过20
		cm.setDefaultMaxPerRoute(HTTP_DEFAULT_MAX_PERROUTE);
		//官方推荐使用这个来检查永久链接的可用性，而不推荐每次请求的时候才去检查
		cm.setValidateAfterInactivity(IN_ACTIVITY_TIME);
		// 请求重试处理
		httpRequestRetryHandler = new HttpRequestRetryHandler() {
			@Override
			public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
				logger.error(exception.getMessage(), exception);
				// 如果已经重试了3次，就放弃
				if (executionCount >= HTTP_EXECUTION_COUNT) {
					return false;
				}
				// 如果服务器丢掉了连接，那么就重试
				if (exception instanceof NoHttpResponseException) {
					return true;
				}
				//传输异常
				if (exception instanceof InterruptedIOException) {
					return true;
				}
				//连接超时
				if (exception instanceof ConnectTimeoutException) {
					return true;
				}
				//获取一个空闲连接失败 
				if (exception instanceof ConnectionPoolTimeoutException) {
					return true;
				}
				//服务器不可达
				if (exception instanceof UnknownHostException) {
					return true;
				}
				HttpRequest request = HttpClientContext.adapt(context).getRequest();
				//如果请求不是关闭连接的请求
				if (!(request instanceof HttpEntityEnclosingRequest)) {
					return true;
				}
				return false;
			}
		};
		requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(HTTP_TIME_OUT)
				.setConnectTimeout(HTTP_TIME_OUT)
				.setSocketTimeout(HTTP_TIME_OUT).build();
		//保持长连的时间
		ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {
			@Override
			public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
				HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
				while (it.hasNext()) {
					HeaderElement he = it.nextElement();
					String param = he.getName();
					String value = he.getValue();
					if (value != null && param.equalsIgnoreCase("timeout")) {
						return Long.parseLong(value) * 1000;
					}
				}
				// 如果没有约定，则默认定义时长为60s
				return 60 * 1000;
			}
		};
		//设置连接池配置
		httpClient = HttpClients.custom()
				.setConnectionManager(cm)
				.setRetryHandler(httpRequestRetryHandler)
				.setKeepAliveStrategy(myStrategy)
				.setDefaultRequestConfig(requestConfig)
				.build();
	}

	public static class CloseExpiredConnectionsThread extends Thread {
		private final HttpClientConnectionManager connectionManager;
		private final BlockingQueue<Boolean> queue;
		public CloseExpiredConnectionsThread(HttpClientConnectionManager connectionManager,
                                             BlockingQueue<Boolean> queue) {
			super();
			this.connectionManager = connectionManager;
			this.queue = queue;
		}
		@Override
		public void run() {
			try {
				for (int i = 0; i < 6; i++) {
					//wait(10000);
					//10s
					Thread.sleep(10000);
					// 关闭异常连接
					connectionManager.closeExpiredConnections();
					// 关闭5s空闲的连接
					connectionManager.closeIdleConnections(50, TimeUnit.SECONDS);
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}finally{
				queue.poll();
			}
		}
	}
	
	private static BlockingQueue<Boolean> booleanQueue = new ArrayBlockingQueue<Boolean>(1);
	public static void closeExpiredConnections(){
		//一定时间内 只有一个线程去移除异常连接 只有当前线程结束 才能新启线程
		if(booleanQueue.offer(true)){
			new CloseExpiredConnectionsThread(cm, booleanQueue).start();
		}
	}

	public static String doGet(String url, String resultEnc) {
		String result = null;
		CloseableHttpResponse response = null;
		HttpGet httpget = new HttpGet(url);
		try {
			//清理失效链接
			closeExpiredConnections();
			resultEnc = StringUtils.isBlank(resultEnc) ? UTF_8 : resultEnc;
			response = httpClient.execute(httpget, HttpClientContext.create());
			if (response.getStatusLine().getStatusCode() != 200) {
				httpget.abort();
				return null;
			}
			result = EntityUtils.toString(response.getEntity(), resultEnc);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			closeResponse(response);
			httpget.abort();
		}
		return result;
	}

	public static String doGet(String url, String resultEnc, Map<String, String> headers) {
		String result = null;
		CloseableHttpResponse response = null;
		HttpGet httpget = new HttpGet(url);
		try {
			closeExpiredConnections();
			resultEnc = StringUtils.isBlank(resultEnc) ? UTF_8 : resultEnc;
			//请求头
			for(Map.Entry<String, String> e : headers.entrySet()) {
				httpget.addHeader(e.getKey(), e.getValue());
			}
			response = httpClient.execute(httpget, HttpClientContext.create());
			if (response.getStatusLine().getStatusCode() != 200) {
				logger.info("httpClient 请求返回状态码=={}", response.getStatusLine());
				return null;
			}
			result = EntityUtils.toString(response.getEntity(), resultEnc);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			closeResponse(response);
			httpget.abort();
		}
		return result;
	}

	public static String doFormPost(String url, Map<String, String> params, Map<String, String> headers) {
		return doFormPost(url, UTF_8, params, headers);
	}


	public static String doFormPost(String url, String resultEnc, Map<String, String> params, Map<String, String> headers) {
		String result = null;
		CloseableHttpResponse response = null;
		HttpPost httpPost = new HttpPost(url);
		try {
			closeExpiredConnections();
			resultEnc = StringUtils.isBlank(resultEnc) ? UTF_8 : resultEnc;
			//请求头
			BasicHeader[] bhs = buildHeaders(headers);
			httpPost.setHeaders(bhs);
			//请求体
			List<NameValuePair> nvps = buildNameValuePair(params);
			/*EntityBuilder builder = EntityBuilder.create();
			builder.setParameters(nvps);*/
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, UTF_8));
			response = httpClient.execute(httpPost, HttpClientContext.create());
			if (response.getStatusLine().getStatusCode() != 200) {
				httpPost.abort();
				logger.info("httpClient 请求返回状态码=={}", response.getStatusLine());
				return null;
			}
			result = EntityUtils.toString(response.getEntity(), resultEnc);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			closeResponse(response);
			httpPost.abort();
		}
		return result;
	}


	public static String doXmlPost(String url, String xml, String resultEnc) {
		String result = null;
		CloseableHttpResponse response = null;
		HttpPost httpPost = new HttpPost(url);
		try {
			closeExpiredConnections();
			//请求头
			httpPost.addHeader("Content-Type", "text/xml;charset=" + resultEnc);
			httpPost.setEntity(new StringEntity(xml,resultEnc));
			response = httpClient.execute(httpPost, HttpClientContext.create());
			if (response.getStatusLine().getStatusCode() != 200) {
				logger.info("httpClient 请求返回状态码=={}", response.getStatusLine());
				return null;
			}
			result = EntityUtils.toString(response.getEntity(), resultEnc);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			closeResponse(response);
			httpPost.abort();
		}
		return result;
	}

	public static String doJsonPost(String url, boolean isEncodeURL, String params,
			Map<String, String> headers) {
		String result = null;
		CloseableHttpResponse response = null;
		HttpPost httpPost = new HttpPost(url);
		try {
			closeExpiredConnections();
			if(headers == null){
				headers = new HashMap<String,String>();
			}
			headers.put("Content-Type","application/json; charset=UTF-8");
			BasicHeader[] bhs = buildHeaders(headers);
			httpPost.setHeaders(bhs);
			//httpPost.setConfig(requestConfig);
			if (null != params) {
				if (isEncodeURL) {
					httpPost.setEntity(new StringEntity(URLEncoder.encode(params, UTF_8)));
				} else {
					httpPost.setEntity(new StringEntity(params, UTF_8));
				}
			}
			response = httpClient.execute(httpPost, HttpClientContext.create());
			if (response.getStatusLine().getStatusCode() != 200) {
				logger.info("httpClient 请求返回状态码=={}", response.getStatusLine());
				return null;
			}
			result = EntityUtils.toString(response.getEntity(), UTF_8).trim();
		} catch (Exception e) {
			//httpPost.abort();
			logger.error(e.getMessage(), e);
		} finally {
			closeResponse(response);
			httpPost.abort();
		}
		return result;
	}

	public static String doJsonPost(String url, Object params, Map<String, String> headers) {
		String paramsStr = JSON.toJSONString(params);
		return doJsonPost(url, false, paramsStr, headers);
	}

	/**
	 * MAP类型数组转换成NameValuePair类型
	 * 
	 */
	public static List<NameValuePair> buildNameValuePair(Map<String, String> params) {
		List<NameValuePair> nvps = new ArrayList<>();
		if (params != null) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}
		return nvps;
	}

	public static BasicHeader[] buildHeaders(Map<String, String> headers) {
		BasicHeader[] bhs = null;
		if (null != headers && !headers.isEmpty()) {
			bhs = new BasicHeader[headers.size()];
			int i = 0;
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				bhs[i] = new BasicHeader(entry.getKey(), entry.getValue());
				i++;
			}
		}
		return bhs;
	}

	private static void closeResponse(CloseableHttpResponse response){
		if (null != response) {
			try {
				EntityUtils.consume(response.getEntity());
				response.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

}
