package com.xinlian.admin.biz.utils;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import com.xinlian.common.utils.UniqueNoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class AliyunOssService {

    //https://new-cat-card.oss-cn-hongkong.aliyuncs.com/web/WechatIMG192.jpg

    /*private static String endpoint = "https://oss-cn-hongkong.aliyuncs.com";
    private static String accessKeyId = "LTAI4FwjNAscMVKcpUD7mvMi";
    private static String accessKeySecret = "lfuy1YwNvlaOjSgxuAFJODEAELYolV";
    private static String bucketName = "cat-web";
    private static String folder = "news";*/


    public List<String> uploadObjectByMultipartFile(List<MultipartFile> files,String uploadFolder,AliyunOssConfig aliyunOssConfig)throws Exception{
        log.debug("method:aliyunOssConfig:"+ JSONObject.toJSONString(aliyunOssConfig));
        List<String> urls = new ArrayList<>(files.size());
        OSSClient client = new OSSClient(aliyunOssConfig.getOssEndpoint(), aliyunOssConfig.getOssKeyId(), aliyunOssConfig.getOssKeySecret());
        for(MultipartFile file : files){
            String originalFilename = file.getOriginalFilename();
            String[] split = originalFilename.split("\\.");
            String fileName = uploadFolder + "/" + UniqueNoUtil.uuid() + "." + split[1];
            try {
                client.putObject(aliyunOssConfig.getOssUrlBucket(), fileName, file.getInputStream());
                urls.add("https://" + aliyunOssConfig.getOssUrlBucket() + "." + client.getEndpoint().toString().replace("https://", "") + "/" + fileName);
            } catch (IOException e) {
                log.error("出现异常：{}",e.getMessage(), e);
            }
        }
        client.shutdown();
        return urls;
    }


    public String uploadFile(MultipartFile file,String uploadFolder,AliyunOssConfig aliyunOssConfig)throws Exception{
        log.debug("method:aliyunOssConfig:"+ JSONObject.toJSONString(aliyunOssConfig));
        OSSClient client = new OSSClient(aliyunOssConfig.getOssEndpoint(), aliyunOssConfig.getOssKeyId(), aliyunOssConfig.getOssKeySecret());
        String fileName = uploadFolder + "/" + file.getOriginalFilename();
        String url = "";
        try {
            client.putObject(aliyunOssConfig.getOssUrlBucket(), fileName, file.getInputStream());
             url = "https://" + aliyunOssConfig.getOssUrlBucket() + "." + client.getEndpoint().toString().replace("https://", "") + "/" + fileName;
        } catch (IOException e) {
            log.error("出现异常：{}",e.getMessage(), e);
        }finally {
            client.shutdown();
            return url;
        }
    }






}
