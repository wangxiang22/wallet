package com.xinlian.admin.server.controller;


import com.xinlian.admin.biz.service.UploadFileService;
import com.xinlian.common.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(value = "文件上传")
@Controller
@RequestMapping("/upload")
@Slf4j
public class UploadController {

    @Autowired
    private UploadFileService uploadFileService;

    @ApiOperation(value = "oss文件上传", httpMethod = "POST")
    @RequestMapping(value = "/oss", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult<List<String>> ossUpload(HttpServletRequest request) {
        log.error("请求oss上传接口!");
        return uploadFileService.oosUpload(request);
    }


    @ApiOperation(value = "oss文件上传", httpMethod = "POST")
    @RequestMapping(value = "/oss/uploadApp", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult ossUploadFile(@RequestParam MultipartFile file,
                                        @RequestParam String version) {
        log.error("请求oss上传apk文件接口!");
        return uploadFileService.oosUploadApk(version,file);
    }


}
