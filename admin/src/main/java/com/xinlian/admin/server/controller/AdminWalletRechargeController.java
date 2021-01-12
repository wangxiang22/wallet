package com.xinlian.admin.server.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.xinlian.admin.biz.service.WalletInfoService;
import com.xinlian.admin.biz.service.impl.WalletTradeOrderService;
import com.xinlian.admin.server.vo.AdminRechargeListVo;
import com.xinlian.admin.server.vo.AdminRechargeListVoConvertor;
import com.xinlian.admin.server.vo.request.SingleRechargeRequest;
import com.xinlian.biz.model.TWalletInfo;
import com.xinlian.biz.model.TWalletTradeOrder;
import com.xinlian.common.enums.CurrencyEnum;
import com.xinlian.common.enums.WalletTradeOrderStatusEnum;
import com.xinlian.common.enums.WalletTradeSystemCodeEnum;
import com.xinlian.common.enums.WalletTradeTypeEnum;
import com.xinlian.common.response.ResponseResultPage;
import com.xinlian.common.result.BizException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

@RestController
@Slf4j
@Api(value = "钱包后台充值")
@RequestMapping(value = "/walletRecharge")
public class AdminWalletRechargeController {

    @Autowired
    private WalletInfoService walletInfoService;
    @Autowired
    private WalletTradeOrderService walletTradeOrderService;

    @ApiOperation(value = "获取后台充值订单列表")
    @GetMapping(value = "/v1/lists")
    public ResponseResultPage getWaitAuditTradeOrder(
            @ApiParam(value = TWalletTradeOrder.rechargeParams)
            @RequestParam Map<String,Object> searchParams){
        try {
            searchParams.put("tradeSystemCode",WalletTradeSystemCodeEnum.ADMIN_TRADE.getCode());
            PageInfo pageInfo = walletTradeOrderService.queryRechargePage(searchParams);
            List<AdminRechargeListVo> voLists = new AdminRechargeListVoConvertor().convertList(pageInfo.getList());
            pageInfo.setList(voLists);
            return new ResponseResultPage(pageInfo);
        } catch (Exception e) {
            log.error("获取后台充值订单列表异常:{}", e.toString(), e);
            return new ResponseResultPage(false);
        }
    }

//    @ApiOperation(value = "批量充值")
//    @PostMapping(value = "/v1/batchRecharge")
//    @OpeAnnotation(typeName = OperationTypeEnum.OTHER_OPERATE, logLevel = OperationLogLevelEnum.INFO,opeDesc = "批量充值")
//    public ResponseResult batchRecharge(
//            @ApiParam(value = "{\"rechargeTypeName\":\"\",\"remark\":\"\",\"batchRechargeData\":\"文本框批量值\"}")
//            @RequestBody JSONObject jsonObject){
//        try {
//            Map<String,Object> convertParamMap = this.convertToParamMap(jsonObject);
//            walletInfoService.batchRecharge(convertParamMap);
//            return new ResponseResult(new BizException(ErrorInfoEnum.SUCCESS));
//        }catch (BizException e){
//            log.error("批量充值异常:{}", e.toString(), e);
//            return new ResponseResult(e);
//        } catch (Exception e) {
//            log.error("批量充值异常:{}", e.toString(), e);
//            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
//        }
//    }
//
//    @ApiOperation(value = "单个充值")
//    @PostMapping(value = "/v1/singleRecharge")
//    @OpeAnnotation(typeName = OperationTypeEnum.OTHER_OPERATE, logLevel = OperationLogLevelEnum.INFO,opeDesc = "单个充值")
//    public ResponseResult singleRecharge(
//            @ApiParam(value = SingleRechargeRequest.params)
//            @RequestBody SingleRechargeRequest singleRechargeRequest){
//        try {
//            TWalletInfo walletInfo = this.convertToWalletInfo(singleRechargeRequest);
//            TWalletTradeOrder walletTradeOrder = this.createTradeOrder(singleRechargeRequest);
//            walletInfoService.singleRechargeRequest(walletInfo,walletTradeOrder);
//            return new ResponseResult(new BizException(ErrorInfoEnum.SUCCESS));
//        }catch (BizException e){
//            log.error("单个充值异常:{}", e.toString(), e);
//            return new ResponseResult(e);
//        } catch (Exception e) {
//            log.error("单个充值异常:{}", e.toString(), e);
//            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
//        }
//    }

    private TWalletTradeOrder createTradeOrder(SingleRechargeRequest singleRechargeRequest) {
        int currencyId = CurrencyEnum.getCurrencyIdByCurrencyCode(singleRechargeRequest.getCurrencyCode());
        if(currencyId==0){ throw new BizException("找不到对应currencyCode:[{ "+singleRechargeRequest.getCurrencyCode()+"]}");}
        TWalletTradeOrder walletTradeOrder = new TWalletTradeOrder();
        walletTradeOrder.setTradeSystemCode(WalletTradeSystemCodeEnum.ADMIN_TRADE.getCode());
        walletTradeOrder.setUid(singleRechargeRequest.getUid());
        walletTradeOrder.setRemark(singleRechargeRequest.getRemark());
        walletTradeOrder.setTradeCurrencyNum(new BigDecimal(singleRechargeRequest.getRechargeNumber()));
        walletTradeOrder.setDes(singleRechargeRequest.getRechargeTypeName());
        walletTradeOrder.setTradeType(WalletTradeTypeEnum.ADMIN_TOP_UP.getTradeType());
        walletTradeOrder.setCurrencyCode(singleRechargeRequest.getCurrencyCode());
        walletTradeOrder.setCurrencyId(Long.parseLong(currencyId+""));
        walletTradeOrder.setTradeStatus(WalletTradeOrderStatusEnum.TRADE_SUCCESS.getCode());
        walletTradeOrder.setDisposeCheckTime(new Date());
        return walletTradeOrder;
    }

    private TWalletInfo convertToWalletInfo(SingleRechargeRequest singleRechargeRequest){
        TWalletInfo walletInfo = new TWalletInfo();
        int currencyId = CurrencyEnum.getCurrencyIdByCurrencyCode(singleRechargeRequest.getCurrencyCode());
        if(currencyId==0){ throw new BizException("找不到对应currencyCode:[{ "+singleRechargeRequest.getCurrencyCode()+"]}");}
        walletInfo.setCurrencyId(Long.parseLong(currencyId+""));
        walletInfo.setCurrencyCode(singleRechargeRequest.getCurrencyCode());
        walletInfo.setBalanceNum(new BigDecimal(singleRechargeRequest.getRechargeNumber()));
        walletInfo.setUid(singleRechargeRequest.getUid());
        return walletInfo;
    }

    private Map<String,Object> convertToParamMap(JSONObject jsonObject){
        Map<String,Object> convertParamMap = new HashMap<String,Object>();
        String remark = jsonObject.getString("remark");
        String rechargeTypeName = jsonObject.getString("rechargeTypeName");
        String batchRechargeData = jsonObject.getString("batchRechargeData");
        List<TWalletInfo> walletInfos = new ArrayList<>();
        List<TWalletTradeOrder> tradeOrders = new ArrayList<>();
        String [] array = batchRechargeData.split("\n");
        for (int i = 0; i < array.length; i++) {
            if("".equals(array[i].trim())){continue;}
            String [] fieldArray = array[i].split(" ");
            TWalletInfo walletInfo = new TWalletInfo();
            walletInfo.setUid(Long.parseLong(fieldArray[0]));
            int currencyId = CurrencyEnum.getCurrencyIdByCurrencyCode(fieldArray[1]);
            if(currencyId==0) {continue;}
            walletInfo.setCurrencyCode(fieldArray[1]);
            walletInfo.setCurrencyId(Long.parseLong(currencyId+""));
            walletInfo.setBalanceNum(new BigDecimal(fieldArray[2]));
            walletInfos.add(walletInfo);

            //生成单子Bean
            TWalletTradeOrder walletTradeOrder = new TWalletTradeOrder();
            walletTradeOrder.setUid(walletInfo.getUid());
            walletTradeOrder.setRemark(remark);
            walletTradeOrder.setTradeCurrencyNum(walletInfo.getBalanceNum());
            walletTradeOrder.setDes(rechargeTypeName);
            walletTradeOrder.setTradeType(WalletTradeTypeEnum.ADMIN_TOP_UP.getTradeType());
            walletTradeOrder.setCurrencyCode(walletInfo.getCurrencyCode());
            walletTradeOrder.setCurrencyId(walletInfo.getCurrencyId());
            walletTradeOrder.setTradeStatus(WalletTradeOrderStatusEnum.TRADE_SUCCESS.getCode());
            walletTradeOrder.setTradeSystemCode(WalletTradeSystemCodeEnum.ADMIN_TRADE.getCode());
            tradeOrders.add(walletTradeOrder);
        }
        convertParamMap.put("walletInfo",walletInfos);
        convertParamMap.put("tradeOrder",tradeOrders);
        return convertParamMap;
    }


}
