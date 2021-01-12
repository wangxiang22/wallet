package com.xinlian.common.utils;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * com.xinlian.common.utils
 *
 * @author by Song
 * @date 2020/2/19 21:10
 */
@Slf4j
public final class SystemUtils {


    public static String getLocalAddress(){
        String ip = "未获取到本机ip";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ip;
    }

    public static String getV4IP() {
        String ip = "";
        String chinaz = "http://ip.chinaz.com";
        StringBuilder inputLine = new StringBuilder();
        String read = "";
        URL url = null;
        HttpURLConnection urlConnection = null;
        BufferedReader in = null;
        try {
            url = new URL(chinaz);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            while ((read = in.readLine()) != null) {
                inputLine.append(read + "\r\n");
            }
            //System.out.println(inputLine.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        Pattern p = Pattern.compile("\\<dd class\\=\"fz24\">(.*?)\\<\\/dd>");
        Matcher m = p.matcher(inputLine.toString());
        if (m.find()) {
            String ipstr = m.group(1);
            ip = ipstr;
            //System.out.println(ipstr);
        }
        return ip;
    }
    /**
     * 获取系统版本信息
     * @param request
     * @return
     */
    public static String getRequestOsNameAndBrowserName(HttpServletRequest request){
        String userAgent = request.getHeader("user-agent").toLowerCase();
        if(userAgent == null || userAgent.equals("")){
            return "";
        }
        String systenInfo = null;
        //得到用户的操作系统
        if (userAgent.indexOf("windows") >= 0 ) {
            systenInfo = "Windows";
        } else if(userAgent.indexOf("mac") >= 0) {
            systenInfo = "Mac";
        } else if(userAgent.indexOf("x11") >= 0) {
            systenInfo = "Unix";
        } else if(userAgent.indexOf("android") >= 0) {
            systenInfo = "Android";
        } else if(userAgent.indexOf("iphone") >= 0) {
            systenInfo = "IPhone";
        }else{
            systenInfo = "UnKnown, More-Info: "+userAgent;
        }

        /**浏览器**/
        String browser = "";
        if (userAgent.contains("edge")) {
            browser=(userAgent.substring(userAgent.indexOf("edge")).split(" ")[0]).replace("/", "-");
        } else if (userAgent.contains("msie")) {
            String substring=userAgent.substring(userAgent.indexOf("msie")).split(";")[0];
            browser=substring.split(" ")[0].replace("msie", "IE")+"-"+substring.split(" ")[1];
        } else if (userAgent.contains("safari") && userAgent.contains("version")) {
            browser=(userAgent.substring(userAgent.indexOf("safari")).split(" ")[0]).split("/")[0]
                    + "-" +(userAgent.substring(userAgent.indexOf("version")).split(" ")[0]).split("/")[1];
        } else if ( userAgent.contains("opr") || userAgent.contains("opera")) {
            if(userAgent.contains("opera")){
                browser=(userAgent.substring(userAgent.indexOf("Opera")).split(" ")[0]).split("/")[0]
                        +"-"+(userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
            }else if(userAgent.contains("opr")){
                browser=((userAgent.substring(userAgent.indexOf("opr")).split(" ")[0]).replace("/", "-"))
                        .replace("opr", "Opera");
            }

        } else if (userAgent.contains("chrome")) {
            browser=(userAgent.substring(userAgent.indexOf("chrome")).split(" ")[0]).replace("/", "-");
        } else if ((userAgent.indexOf("mozilla/7.0") > -1) || (userAgent.indexOf("netscape6") != -1)  ||
                (userAgent.indexOf("mozilla/4.7") != -1) || (userAgent.indexOf("mozilla/4.78") != -1) ||
                (userAgent.indexOf("mozilla/4.08") != -1) || (userAgent.indexOf("mozilla/3") != -1) ) {
            browser = "Netscape-?";

        } else if (userAgent.contains("firefox")) {
            browser=(userAgent.substring(userAgent.indexOf("firefox")).split(" ")[0]).replace("/", "-");
        } else if(userAgent.contains("rv")) {
            String IEVersion = (userAgent.substring(userAgent.indexOf("rv")).split(" ")[0]).replace("rv:", "-");
            browser="IE" + IEVersion.substring(0,IEVersion.length() - 1);
        } else {
            browser = "UnKnown, More-Info: "+userAgent;
        }
        return systenInfo + "-" + browser;
    }

    public static String getIpAddress(HttpServletRequest request){
        String ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            //log.info(DateFormatUtil.getByNowTime(7).concat("HTTP_X_FORWARDED_FOR 获取为空"));
            ip = request.getHeader("x-forwarded-for");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            //log.info(DateFormatUtil.getByNowTime(7).concat("x-forwarded-for 获取为空"));
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            //log.info(DateFormatUtil.getByNowTime(7).concat("Proxy-Client-IP 获取为空"));
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            //log.info(DateFormatUtil.getByNowTime(7).concat("WL-Proxy-Client-IP 获取为空"));
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            //log.info(DateFormatUtil.getByNowTime(7).concat("HTTP_CLIENT_IP 获取为空"));
            ip = request.getRemoteAddr();
            if(ip.equals("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1")){
                //根据网卡取本机配置的IP
                InetAddress inet=null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ip= inet.getHostAddress();
            }
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if(ip!=null && ip.length()>15) { //"***.***.***.***".length() = 15
            //log.info(DateFormatUtil.getByNowTime(7) + "获取客户端所在IP:{}",ip);
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }

}
