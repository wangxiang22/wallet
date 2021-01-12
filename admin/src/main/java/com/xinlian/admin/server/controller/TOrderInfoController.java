package com.xinlian.admin.server.controller;

import com.xinlian.admin.biz.service.TOrderInfoService;
import com.xinlian.common.request.OrderInfoRealTimePageReq;
import com.xinlian.common.request.OrderInfoTotalAmountReq;
import com.xinlian.common.request.QuerySpotInfoReq;
import com.xinlian.common.response.OrderInfoRealTimeRes;
import com.xinlian.common.response.OrderInfoTotalAmountRes;
import com.xinlian.common.response.PageResult;
import com.xinlian.common.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author lx
 * @since 2020-06-19
 */
@Controller
@RequestMapping("/tOrderInfo")
@Api("订单详情")
public class TOrderInfoController {
    @Autowired
    private TOrderInfoService tOrderInfoService;

    @ApiOperation("查询交易明细")
    @PostMapping("querySpotInfo")
    @ResponseBody
    public ResponseResult querySpotInfo(@RequestBody QuerySpotInfoReq querySpotInfo) {
        return tOrderInfoService.querySpotInfo(querySpotInfo);
    }

    @ApiOperation("导出交易明细")
    @PostMapping("exportSpotInfo")
    @ResponseBody
    public ResponseResult exportSpotInfo(@RequestBody QuerySpotInfoReq querySpotInfo, HttpServletResponse httpServletResponse) {
        return tOrderInfoService.exportSpotInfo(querySpotInfo, httpServletResponse);
    }

    @ApiOperation("导出各状态订单列表查询")
    @PostMapping("exportQueryEveryOrderList")
    @ResponseBody
    public ResponseResult exportQueryEveryOrderList(@RequestBody QuerySpotInfoReq querySpotInfo) {
        return tOrderInfoService.exportQueryEveryOrderList(querySpotInfo);
    }

    @ApiOperation("各状态订单列表查询")
    @PostMapping("queryEveryOrderList")
    @ResponseBody
    public ResponseResult queryEveryOrderList(@RequestBody QuerySpotInfoReq querySpotInfo) {
        return tOrderInfoService.queryEveryOrderList(querySpotInfo);
    }

    @ApiOperation("根据地址查买家信息")
    @GetMapping("queryBuyerInfobyAddr")
    @ResponseBody
    public ResponseResult queryBuyerInfoByAddr(@RequestParam String addr) {
        return tOrderInfoService.queryBuyerInfoByAddr(addr);
    }

    @ApiOperation("分页查询今日实时订单明细/历史账单明细")
    @PostMapping("/findRealTimeOrderInfoPage")
    @ResponseBody
    public PageResult<List<OrderInfoRealTimeRes>> findRealTimeOrderInfoPage(@RequestBody OrderInfoRealTimePageReq req) {
        return tOrderInfoService.findRealTimeOrderInfoPage(req);
    }

    @ApiOperation("查询今日实时订单明细/历史账单明细（不分页）")
    @PostMapping("/findRealTimeOrderInfo")
    @ResponseBody
    public ResponseResult<List<OrderInfoRealTimeRes>> findRealTimeOrderInfo(@RequestBody OrderInfoRealTimePageReq req) {
        return tOrderInfoService.findRealTimeOrderInfo(req);
    }

    @ApiOperation("查询买卖家总出入金及出入金差额")
    @PostMapping("/findSellerBuyerAltogether")
    @ResponseBody
    public ResponseResult<OrderInfoTotalAmountRes> findSellerBuyerAltogether(@RequestBody OrderInfoTotalAmountReq req) {
        return tOrderInfoService.findSellerBuyerAltogether(req);
    }

}

