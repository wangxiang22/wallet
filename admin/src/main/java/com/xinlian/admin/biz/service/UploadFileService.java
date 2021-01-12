package com.xinlian.admin.biz.service;

import com.xinlian.common.response.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UploadFileService {

    /**
     * 上传 文件到阿里云
     * @param request
     * @return
     */
    ResponseResult<List<String>> oosUpload(HttpServletRequest request);




    /**
     * 上传 文件到阿里云
     * @param version
     * @param file
     * @return
     */
    ResponseResult oosUploadApk(String version, MultipartFile file);
}
