package com.xinlian.admin.server.controller;

import com.github.pagehelper.PageInfo;
import com.xinlian.admin.biz.service.OperationLogService;
import com.xinlian.biz.model.OperationLogModel;
import com.xinlian.common.response.ResponseResultPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * com.xinlian.admin.server.controller
 *
 * @author by Song
 * @date 2020/2/20 21:58
 */
@Api(value = "运营后台系统-日志管理")
@RestController
@RequestMapping("/index")
@Slf4j
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    /**
     * 日志查询接口
     * @param searchParams
     * @return
     */
    @ApiOperation(value = "获取日志查询接口")
    @GetMapping(value = "/v1/opeLogList")
    public ResponseResultPage queryOpeLogList(
            @ApiParam(value = OperationLogModel.PARAMS)
            @RequestParam Map<String,Object> searchParams){
        try {
            PageInfo pageInfo = operationLogService.queryPage(searchParams);
            //List<WalletTradeFlowVo> voLists = new WalletTradeFlowVoConvertor().convertList(pageInfo.getList());
            //pageInfo.setList(voLists);
            return new ResponseResultPage(pageInfo);
        } catch (Exception e) {
            log.error("获取日志查询接口异常:{}", e.toString(), e);
            return new ResponseResultPage(false);
        }
    }
}
