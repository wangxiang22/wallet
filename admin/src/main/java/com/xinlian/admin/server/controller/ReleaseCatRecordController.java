package com.xinlian.admin.server.controller;

import com.xinlian.admin.biz.service.TReleaseCatRecordService;
import com.xinlian.admin.biz.service.TUploadChainOwnerRecordService;
import com.xinlian.admin.biz.utils.AdminExcelTitleConfig;
import com.xinlian.admin.biz.utils.ParseExcelUtil;
import com.xinlian.biz.model.TReleaseCatRecord;
import com.xinlian.biz.model.TUploadChainOwnerRecord;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import com.xinlian.common.result.ErrorInfoEnum;
import com.xinlian.common.utils.ListUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Api(value = "释放债权人cat控制")
@RestController
@RequestMapping("/release")
@Slf4j
public class ReleaseCatRecordController {

    @Autowired
    private TReleaseCatRecordService releaseCatRecordService;
    @Autowired
    private TUploadChainOwnerRecordService uploadChainOwnerRecordService;

    @PostMapping(value = "/v1/upload/releaseCat")
    @ApiOperation(value = "上传-链权释放CAT数据")
    public ResponseResult importReleaseCatFile(HttpServletRequest httpServletRequest, @RequestParam MultipartFile file){
        try {
            String uploadKey = httpServletRequest.getParameter("uploadKey");
            //解析文件
            if (file.getSize() > 10000000) {
                throw new Exception("上传失败：文件大小不能超过10M");
            }
            Map<String, Map<String, String>> fieldValuesRange = new HashMap<>();

            List<TReleaseCatRecord> models = ParseExcelUtil.excelToList("", file, TReleaseCatRecord.class, AdminExcelTitleConfig.RELEASE_CAT_RECORD_MAP, 0, fieldValuesRange);

            int num = models.size() % 1000 == 0 ? models.size() / 1000 : models.size() / 1000 + 1;
            List<List<TReleaseCatRecord>> subLists = ListUtil.averageAssign(models, num);
            subLists.forEach(subList -> {
                Map<String, Object> params = new HashMap();
                params.put("insertList", subList);
                releaseCatRecordService.batchInsert(params);
            });
            return new ResponseResult(new BizException(ErrorInfoEnum.SUCCESS));
        }catch (DataIntegrityViolationException e){
            log.error("导入链权释放CAT数据出现异常:{}", e.toString(),e);
            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
        } catch (Exception e) {
            log.error("导入链权释放CAT数据异常:{}", e.toString(),e);
            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
        }
    }

    @PostMapping(value = "/v1/upload/chainOwnerRecord")
    @ApiOperation(value = "上传-链权人数据")
    public ResponseResult chainOwnerRecord(HttpServletRequest httpServletRequest, @RequestParam MultipartFile file){
        try {
            //解析文件
            if (file.getSize() > 10000000) {
                throw new Exception("上传失败：文件大小不能超过10M");
            }
            Map<String, Map<String, String>> fieldValuesRange = new HashMap<>();
            List<TUploadChainOwnerRecord> models = ParseExcelUtil.excelToList("", file, TUploadChainOwnerRecord.class, AdminExcelTitleConfig.WAIT_DISPOSE_CHAIN_OWNER_MAP, 0, fieldValuesRange);
            int num = models.size() % 1000 == 0 ? models.size() / 1000 : models.size() / 1000 + 1;
            List<List<TUploadChainOwnerRecord>> subLists = ListUtil.averageAssign(models, num);
            subLists.forEach(subList -> {
                uploadChainOwnerRecordService.batchInsert(subList);
            });
            return new ResponseResult(new BizException(ErrorInfoEnum.SUCCESS));
        }catch (DataIntegrityViolationException e){
            log.error("导入链权人数据出现异常:{}", e.toString(),e);
            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
        } catch (Exception e) {
            log.error("导入链权人数据异常:{}", e.toString(),e);
            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
        }
    }

}
