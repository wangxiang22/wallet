package com.xinlian.admin.biz.service.impl;

import com.xinlian.admin.biz.service.UploadFileService;
import com.xinlian.admin.biz.utils.AliyunOssConfig;
import com.xinlian.admin.biz.utils.AliyunOssService;
import com.xinlian.biz.utils.AdminOptionsUtil;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
import com.xinlian.common.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class UploadFileServiceImpl implements UploadFileService {

    @Autowired
    private AliyunOssService aliyunOssService;
    @Autowired
    private AliyunOssConfig aliyunOssConfig;
    @Autowired
    private AdminOptionsUtil adminOptionsUtil;

    @Override
    public ResponseResult<List<String>> oosUpload(HttpServletRequest request){
        ResponseResult<List<String>> result = new ResponseResult<>();
        result.setCode(GlobalConstant.ResponseCode.FAIL);
        try{
            //MultipartHttpServletRequest params = ((MultipartHttpServletRequest) request);
            //params.getPart();

            List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("myFileName");
            if(files==null || files.isEmpty()){
                result.setMsg("无图片");
                return result;
            }
            if(files.size() > 10){
                result.setMsg("一次上传图片不可超过10张");
                return result;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            //Bean有值，无论修改啥，都无法更改bean中的值
            aliyunOssConfig = adminOptionsUtil.fieldEntityObject(AdminOptionsBelongsSystemCodeEnum.ADMIN_OSS.getBelongsSystemCode(),AliyunOssConfig.class);

            //List<String> urls = OssUtil.uploadObjectByMultipartFile(files,aliyunOssConfig.getOssFolder() +"/"+ sdf.format(new Date()));
            List<String> urls = aliyunOssService.uploadObjectByMultipartFile(files,aliyunOssConfig.getOssFolder() +"/"+ sdf.format(new Date()),aliyunOssConfig);
            if(urls.isEmpty()){
                result.setMsg("上传失败");
                return result;
            }
            result.setCode(GlobalConstant.ResponseCode.SUCCESS);
            result.setResult(urls);
            return result;
        }catch (Exception e){
            log.error("上传出现异常：{}",e.toString(),e);
            result.setMsg("上传失败");
            return result;
        }
    }

    @Override
    public ResponseResult oosUploadApk(String version,MultipartFile file) {
        ResponseResult result = new ResponseResult<>();
        result.setCode(GlobalConstant.ResponseCode.FAIL);
        try{
            aliyunOssConfig = adminOptionsUtil.fieldEntityObject(AdminOptionsBelongsSystemCodeEnum.ADMIN_OSS_UPLOAD.getBelongsSystemCode(),AliyunOssConfig.class);
            String url = aliyunOssService.uploadFile(file,aliyunOssConfig.getOssFolder() +"/"+ version,aliyunOssConfig);
            if(url.isEmpty()){
                result.setMsg("上传文件失败");
                return result;
            }
            //url - 替换成CDN加速后的地址
            //http://cdn-columbu-static.nsh.pub/package/
            int subStringIndex = url.indexOf(aliyunOssConfig.getOssFolder());
            String cdnUrl = "http://cdn-columbu-static.nsh.pub/".concat(url.substring(subStringIndex));
            result.setCode(GlobalConstant.ResponseCode.SUCCESS);
            result.setResult(cdnUrl);
            return result;
        }catch (Exception e){
            log.error("上传出现异常：{}",e.toString(),e);
            result.setMsg("上传失败");
            return result;
        }
    }

    public static void main(String[] args) {
        String originalFilename = "CAT内测_v3.1.9.apk";
        int beginIndex = originalFilename.indexOf("_")+1;
        int lastIndex = originalFilename.lastIndexOf(".");
        System.err.println(originalFilename.substring(beginIndex,lastIndex));
    }
}
