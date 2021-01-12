package com.xinlian.admin.biz.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.xinlian.common.utils.UniqueNoUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class OssUtil {
    protected final static Logger logger = LoggerFactory.getLogger(OssUtil.class);

    //https://new-cat-card.oss-cn-hongkong.aliyuncs.com/web/WechatIMG192.jpg

    /*private static String endpoint = "https://oss-cn-hongkong.aliyuncs.com";
    private static String accessKeyId = "LTAI4FnxNT6Pn4GwFV8hiaZq";
    private static String accessKeySecret = "efJHuR3UWCHttbVv7hX7tr6rQ9U6Y2";
    private static String bucketName = "new-cat-card";*/

    private static String endpoint = "https://oss-cn-hongkong.aliyuncs.com";
    private static String accessKeyId = "LTAI4FwjNAscMVKcpUD7mvMi";
    private static String accessKeySecret = "lfuy1YwNvlaOjSgxuAFJODEAELYolV";
    private static String bucketName = "cat-web";
    private static String folder = "news";

    public static List<String> uploadObjectByMultipartFile(List<MultipartFile> files, String folder){
        List<String> urls = new ArrayList<>(files.size());
        OSSClient client = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        for(MultipartFile file : files){
            String originalFilename = file.getOriginalFilename();
            String[] split = originalFilename.split("\\.");
            String fileName = folder + "/" + UniqueNoUtil.uuid() + "." + split[1];
            try {
                client.putObject(bucketName, fileName, file.getInputStream());
                urls.add("https://" + bucketName + "." + client.getEndpoint().toString().replace("https://", "") + "/" + fileName);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        client.shutdown();
        return urls;
    }


    /**
     * 上传文件-文件夹拼接(a/b/c/+filename)(流的形式-ecs免流量)
     * @param inputStream 文件流
     * @param folder 文件夹
     * @param fileName 文件名字(保留后缀)
     * @return 保存的文件key
     */
    public static String uploadObjectByInputStream(InputStream inputStream, String folder, String fileName){
        OSS client = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        //folder+/+fileName
        String key;
        if(StringUtils.isEmpty(folder)){
            key = fileName;
        }else{
            key = folder + fileName;
        }
        try{
            client.putObject(bucketName, key, inputStream);
        }catch (Exception e){
            throw e;
        }finally {
            client.shutdown();
        }
        return key;
    }

    /**
     * 上传文件-全路径(a/b/c/filename)(流的形式-ecs免流量)
     * @param inputStream 文件流
     * @param fileFullPath 文件全路径(保留后缀)
     * @return 保存的文件key
     */
    public static String uploadObjectByInputStream(InputStream inputStream, String fileFullPath) throws Exception{
        OSS client = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try{
            client.putObject(bucketName, fileFullPath, inputStream);
        }catch (Exception e){
            throw e;
        }finally {
            client.shutdown();
        }
        return fileFullPath;
    }

    /**
     * 通过oss->File->key值 获取对应文件的流(字节-网络传输)
     * @param fileKey
     * @return
     */
    public static byte[] getFileByteByKey(String fileKey) throws Exception {
        OSS client = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        InputStream inputStream = null;
        byte[] result = null;
        try {
            OSSObject ossObject = client.getObject(bucketName, fileKey);
            inputStream = ossObject.getObjectContent();
            result = toByteArray(inputStream);
        }catch (Exception e){
            throw e;
        }finally {
            try{
                if(inputStream!=null){
                    inputStream.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            client.shutdown();
        }
        return result;
    }

    /**
     * 通过key值 获取对应文件的流(InputStream)
     * 实际上这个方法是无效的(client.shutdwon()会将流关闭 )
     * 所以可以这里进行流的操作 (比如可以参见:getFileByteByKey())
     * 把流读取到可以被保存的数据媒介中
     * @param fileKey
     * @return
     */
    @Deprecated
    public static InputStream getFileInputStreamByKey(String fileKey) {
        OSS client = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        InputStream inputStream = null;
        try {
            OSSObject ossObject = client.getObject(bucketName, fileKey);
            inputStream = ossObject.getObjectContent();
        }catch (Exception e){
            throw e;
        }finally {
            try{
                if(inputStream!=null){
                    inputStream.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            client.shutdown();
        }
        return inputStream;
    }


    /**
     * 根据文件名获取文件访问URL(外网访问-下行收费)
     * @param ossFileKey 文件名
     * @param expires URL有效时间（小时）
     * @return
     */
    public static String getFileUrl(String ossFileKey, int expires) {
        OSS client = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        URL url = client.generatePresignedUrl(bucketName, ossFileKey, dateAfter(new Date(), expires, Calendar.HOUR));
        String path = url.toString();
        return path;
    }


    /**
     * 原图生成缩略图url
     * @param ossFileKey
     * @return
     */
    public static String getSltFileUrl(String ossFileKey,String centerName) throws IOException {
        OSS client = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        /**
         * 控制台设置的图片处理的style或者默认style
         */
        String style = "image/auto-orient,1/resize,m_lfit,w_100/quality,q_90";
        GetObjectRequest request = new GetObjectRequest(bucketName,ossFileKey);
        request.setProcess(style);
        OSSObject ossObject = client.getObject(request);
        InputStream inputStream = ossObject.getObjectContent();
        String suffix = ossFileKey.substring(ossFileKey.lastIndexOf("."));
        String name = ossFileKey.substring(0,ossFileKey.lastIndexOf("."));
        String sltName = name+centerName+suffix;
        System.out.println(sltName);
        try{
            client.putObject(bucketName, sltName, inputStream);
        }catch (Exception e){
            throw e;
        }finally {
            client.shutdown();
        }
        return sltName;
    }


    /**
     * (private)input转字节
     * @param input
     * @return
     * @throws Exception
     */
    private static byte[] toByteArray(InputStream input) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    /**
     * (private)获得指定日期之后一段时期的日期。例如某日期之后3天的日期等。
     * @param origDate 基准日期
     * @param amount 基准日期
     * @param timeUnit 时间单位，如年、月、日等。用Calendar中的常量代表
     * @return
     */
    private static final Date dateAfter(Date origDate, int amount, int timeUnit) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(origDate);
        calendar.add(timeUnit, amount);
        return calendar.getTime();
    }

    public static void main(String[] args) throws Exception {
        FileInputStream fileInputStream = new FileInputStream("E:\\area.json");
        OSSClient client = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        String fileName = "news" + "/" + UniqueNoUtil.uuid() + ".json";
        client.putObject(bucketName, fileName, fileInputStream);
        System.out.println("https://" + bucketName + "." + client.getEndpoint().toString().replace("https://", "") + "/" + fileName);
        client.shutdown();
        fileInputStream.close();
    }

}
