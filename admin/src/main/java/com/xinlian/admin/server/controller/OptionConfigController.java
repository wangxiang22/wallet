package com.xinlian.admin.server.controller;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.admin.biz.service.AdminOptionsService;
import com.xinlian.admin.server.operationLog.OpeAnnotation;
import com.xinlian.admin.server.vo.AdminOptionsVo;
import com.xinlian.admin.server.vo.AdminOptionsVoConvertor;
import com.xinlian.biz.model.AdminOptions;
import com.xinlian.common.enums.OperationLogLevelEnum;
import com.xinlian.common.enums.OperationTypeEnum;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import com.xinlian.common.result.ErrorInfoEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Api(value = "运营后台系统-日志管理")
@RestController
@RequestMapping("/option")
@Slf4j
public class OptionConfigController {

    @Autowired
    private AdminOptionsService adminOptionsService;


    /**
     * 查询配置信息接口
     * @param belongsSystemCode 所属belongsSystemCode
     * @return
     */
    @ApiOperation(value = "查询配置信息接口")
    @GetMapping(value = "/v1/configInfo")
    public ResponseResult queryConfigInfo(
            @ApiParam(name = "belongsSystemCode",value = "BASIC_CONFIG:基础配置项；APP_ACT:APP活动；ADMIN_OSS:ADMIN上传图片OSS配置")
            @RequestParam String belongsSystemCode){
        try {
            AdminOptions adminOptions = new AdminOptions();
            adminOptions.setBelongsSystemCode(belongsSystemCode);
            adminOptions.setIsShow(1);
            List<AdminOptions> optionsList = adminOptionsService.queryByBelongsSystemCode(adminOptions);
            List<AdminOptionsVo> voList = new AdminOptionsVoConvertor().convertList(optionsList);
            return new ResponseResult(voList);
        } catch (Exception e) {
            log.error("查询配置信息接口异常:{}", e.toString(), e);
            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
        }
    }

    /**
     * @see com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum
     * @param jsonObject
     * @return
     */
    @ApiOperation(value = "修改配置信息接口")
    @PostMapping(value = "/v1/configInfo/update")
    @OpeAnnotation(typeName = OperationTypeEnum.OTHER_OPERATE, logLevel = OperationLogLevelEnum.INFO,opeDesc = "修改配置信息")
    public ResponseResult updateConfigInfo(
            @ApiParam(name = "jsonObject",required = true,value = "belongsSystemCode必传字段(固定key)，需要修改字段key以及值 - " +
                    "传值修改成：{\"belongsSystemCodeValue2\":{\"key2\":\"value1\"},\"belongsSystemCodeValue2\":{\"key1\":\"value1\"}}")
            @RequestBody JSONObject jsonObject ){
        try {
            for(String belongsSystemCodeKey : jsonObject.keySet()){
               JSONObject childJson = jsonObject.getJSONObject(belongsSystemCodeKey);
                List<AdminOptions> adminOptionsList = new ArrayList<AdminOptions>();
                for(String childKey : childJson.keySet()) {
                    if ("belongsSystemCode" != childKey) {
                        AdminOptions adminOptions = new AdminOptions();
                        adminOptions.setOptionName(childKey);
                        adminOptions.setOptionValue(childJson.getString(childKey));
                        adminOptions.setBelongsSystemCode(belongsSystemCodeKey);
                        adminOptionsList.add(adminOptions);
                    }
                }
                if(adminOptionsList.size()>0) {
                    adminOptionsService.batchUpdateModel(adminOptionsList);
                    //清除缓存
                    adminOptionsService.removeRedisCache(belongsSystemCodeKey,adminOptionsList);
                }
            }
            return new ResponseResult(new BizException(ErrorInfoEnum.SUCCESS));
        } catch (BizException e) {
            log.error("修改配置信息接口异常:{}", e.toString(), e);
            return new ResponseResult(e);
        } catch (Exception e) {
            log.error("修改配置信息接口异常:{}", e.toString(), e);
            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
        }
    }

    public static void main(String[] args) {
        String jsonStr = "{\n" +
                "\t\"de\": {\n" +
                "\t\t\"de-de\": \"de-de-value\"\n" +
                "\t},\n" +
                "\t\"de1\": {\n" +
                "\t\t\"de1-de1\": \"de1-de1-value\"\n" +
                "\t}\n" +
                "}";
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        for(String key : jsonObject.keySet()){
            System.err.println(key);
            Object d = jsonObject.get(key);
            System.err.println(d.toString());
        }
    }
}
