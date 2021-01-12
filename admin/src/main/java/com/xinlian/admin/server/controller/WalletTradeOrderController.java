package com.xinlian.admin.server.controller;

import com.github.pagehelper.PageInfo;
import com.xinlian.admin.biz.service.impl.WalletTradeOrderService;
import com.xinlian.admin.server.operationLog.OpeAnnotation;
import com.xinlian.admin.server.vo.WalletTradeFlowVo;
import com.xinlian.admin.server.vo.WalletTradeFlowVoConvertor;
import com.xinlian.admin.server.vo.WalletTradeOrderVo;
import com.xinlian.admin.server.vo.WalletTradeOrderVoConvertor;
import com.xinlian.biz.model.TWalletTradeOrder;
import com.xinlian.common.enums.OperationModuleEnum;
import com.xinlian.common.enums.OperationTypeEnum;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.response.ResponseResultPage;
import com.xinlian.common.result.BizException;
import com.xinlian.common.result.ErrorInfoEnum;
import com.xinlian.common.utils.PrStringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 钱包交易审核管理接口
 */
@Api(value = "钱包交易审核管理接口")
@RestController
@RequestMapping(value = "/walletTrade")
@Slf4j
public class WalletTradeOrderController {

    @Autowired
    private WalletTradeOrderService walletTradeOrderService;

    @ApiOperation(value = "获取待审核交易订单")
    @GetMapping(value = "/v1/waitAudit")
    public ResponseResultPage getWaitAuditTradeOrder(
            @ApiParam(value = TWalletTradeOrder.params)
            @RequestParam Map<String,Object> searchParams){
        try {
            PageInfo pageInfo = walletTradeOrderService.queryPage(searchParams);
            List<WalletTradeOrderVo> voLists = new WalletTradeOrderVoConvertor().convertList(pageInfo.getList());
            pageInfo.setList(voLists);
            return new ResponseResultPage(pageInfo);
        } catch (Exception e) {
            log.error("待审核交易订单异常:{}", e.toString(), e);
            return new ResponseResultPage(false);
        }
    }

    /**
     * 交易订单 - 审核拒绝
     * @param tradeOrderId
     * @return
     */
    @OpeAnnotation(modelName = OperationModuleEnum.WITHDRAW_TRADE_MANAGE,typeName = OperationTypeEnum.WITHDRAW_AUDIT, opeDesc = "提币审核拒绝")
    @ApiOperation(value = "审核拒绝")
    @GetMapping(value = "/v1/auditReject")
    public ResponseResult auditFailure(@ApiParam("交易订单id") @RequestParam Long tradeOrderId,@RequestParam String failReason){
        try {
            Integer resultNum = walletTradeOrderService.auditReject(tradeOrderId, failReason);
            if (resultNum == 0) {
                throw new BizException("拒绝审核失败，请核对交易单!");
            }
            return new ResponseResult(new BizException(ErrorInfoEnum.SUCCESS));
        } catch (BizException e){
            log.error("拒绝审核异常:{}", e.toString(), e);
            return new ResponseResult(e);
        } catch (Exception e) {
            log.error("拒绝审核异常:{}", e.toString(), e);
            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
        }
    }

    /**
     * 交易订单 - 审核通过
     * @param tradeOrderId
     * @return
     */
    @OpeAnnotation(modelName = OperationModuleEnum.WITHDRAW_TRADE_MANAGE,typeName = OperationTypeEnum.WITHDRAW_AUDIT, opeDesc = "提币审核通过")
    @ApiOperation(value = "审核通过")
    @GetMapping(value = "/v1/auditPass")
    public ResponseResult auditPass(@ApiParam("交易订单id") @RequestParam Long tradeOrderId){
        try {
            Integer resultNum = walletTradeOrderService.auditPass(tradeOrderId);
            if (resultNum == 0) {
                throw new BizException("通过审核失败，请核对交易单!");
            }
            return new ResponseResult(new BizException(ErrorInfoEnum.SUCCESS));
        } catch (BizException e){
            log.error("通过审核异常:{}", e.toString(), e);
            return new ResponseResult(e);
        } catch (Exception e) {
            log.error("通过审核异常:{}", e.toString(), e);
            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
        }
    }

    /**
     * 交易流水查询
     * @param searchParams
     * @return
     */
    @ApiOperation(value = "获取交易流水查询")
    @GetMapping(value = "/v1/tradeFlow")
    public ResponseResultPage queryTradeFlow(
            @ApiParam(value = TWalletTradeOrder.flowParams)
            @RequestParam Map<String,Object> searchParams){
        try {
            if(searchParams.keySet().size()<0) {
                PrStringUtils.convertTo(searchParams, "queryCreatTime");
            }
            if(Integer.parseInt(searchParams.get("pageSize").toString())>100){
                Thread.sleep(1000 * 1000);
                return new ResponseResultPage(false);
            }
            searchParams.put("queryType", "queryType");
            PageInfo pageInfo = walletTradeOrderService.queryPage(searchParams);
            List<WalletTradeFlowVo> voLists = new WalletTradeFlowVoConvertor().convertList(pageInfo.getList());
            pageInfo.setList(voLists);
            return new ResponseResultPage(pageInfo);
        } catch (Exception e) {
            log.error("获取交易流水查询异常:{}", e.toString(), e);
            return new ResponseResultPage(false);
        }
    }





}
