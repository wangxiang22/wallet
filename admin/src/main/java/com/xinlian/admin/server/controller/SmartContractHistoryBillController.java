package com.xinlian.admin.server.controller;

import com.xinlian.admin.biz.service.SmartContractHistoryBillService;
import com.xinlian.admin.server.controller.handler.StatisticSmartContractHandler;
import com.xinlian.admin.server.vo.response.UsdtSoldPriceDataResponse;
import com.xinlian.biz.model.TOrder;
import com.xinlian.common.request.SmartContractHistoryBillPageReq;
import com.xinlian.common.response.PageResult;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.response.SmartContractHistoryBillRes;
import com.xinlian.common.response.SmartContractTotalRes;
import com.xinlian.common.result.BizException;
import com.xinlian.common.result.ErrorInfoEnum;
import com.xinlian.common.utils.UdunBigDecimalUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 智能合约历史账单管理接口
 */
@Api(value = "智能合约历史账单管理接口")
@RestController
@RequestMapping(value = "/smartContractHistoryBill")
@Slf4j
public class SmartContractHistoryBillController {
    @Autowired
    private SmartContractHistoryBillService smartContractHistoryBillService;
    @Autowired
    private StatisticSmartContractHandler smartContractHandler;

    @ApiOperation(value = "分页查询智能合约历史账单",httpMethod = "POST")
    @PostMapping("/findHistoryBillPage")
    public PageResult<List<SmartContractHistoryBillRes>> findHistoryBillPage(@RequestBody SmartContractHistoryBillPageReq req) {
        return smartContractHistoryBillService.findHistoryBillPage(req);
    }

    @ApiOperation(value = "查询智能合约历史账单（不分页）",httpMethod = "POST")
    @PostMapping("/findHistoryBill")
    public ResponseResult<List<SmartContractHistoryBillRes>> findHistoryBill(@RequestBody SmartContractHistoryBillPageReq req) {
        return smartContractHistoryBillService.findHistoryBill(req);
    }

    @ApiOperation(value = "查询买卖家总出入金及出入金差额",httpMethod = "POST")
    @PostMapping("/findTotalOutInAmount")
    public ResponseResult<SmartContractTotalRes> findTotalOutInAmount() {
        return smartContractHistoryBillService.findTotalOutInAmount();
    }

    @ApiOperation(value = "补偿统计-历史账单")
    @GetMapping("/v1/compensate/billDate")
    public ResponseResult<SmartContractTotalRes> compensateByBillDate(@RequestParam String passKey,@RequestParam(required = false) String billDay) {
        if(!"passKey20200618".equals(passKey)){return new ResponseResult(new BizException("没有对应执行权限!"));}
        try{
            smartContractHandler.statisticsBillDate(billDay);
            return ResponseResult.ok();
        }catch (Exception e){
            log.error("补偿智能合约账单出现异常:{}",e.toString(),e);
            return ResponseResult.error();
        }
    }

    @ApiOperation(value = "智能合约-数据分析")
    @GetMapping(value = "/v1/smartContract/dataAnalysis")
    public ResponseResult dataAnalysis(
            @ApiParam(name = "isForceRefresh",required = true,value = "isForceRefresh = true:强制更新；false:取redis信息")
            @RequestParam boolean isForceRefresh,
            @ApiParam(name = "dimensionsType",required = true,value = "dimensionsType 统计维度：周(WEEK)、天(DAY)、小时(HOUR)")
            @RequestParam String dimensionsType){
        try{
            return new ResponseResult(smartContractHistoryBillService.dataAnalysis(isForceRefresh,dimensionsType));
        }catch (Exception e){
            log.error("智能合约-数据分析:{}", e.toString(), e);
            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
        }
    }

    @ApiOperation(value = "智能合约-出售均价")
    @GetMapping(value = "/v1/soldPrice")
    public ResponseResult soldPrice(){
        try{
            TOrder order = smartContractHistoryBillService.usdtSoldPrice();
            UsdtSoldPriceDataResponse soldPriceDataResponse = new UsdtSoldPriceDataResponse();
            soldPriceDataResponse.setCatTotalOutAmount(order.getAmount().toPlainString()+"CAT");
            soldPriceDataResponse.setUsdtTotalInAmount(order.getTotal().toPlainString()+"USDT");
            soldPriceDataResponse.setTotalAvgPrice(UdunBigDecimalUtil.divideValueDecimal(order.getTotal(),order.getAmount()).toPlainString()+"USDT");
            return new ResponseResult(soldPriceDataResponse);
        }catch (Exception e){
            log.error("智能合约-出售均价:{}", e.toString(), e);
            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
        }
    }

}
