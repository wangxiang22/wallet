package com.xinlian.admin.server.controller;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.xinlian.admin.biz.redis.RedisClient;
import com.xinlian.admin.biz.service.TNewOrderService;
import com.xinlian.admin.server.operationLog.OpeAnnotation;
import com.xinlian.biz.dao.mapper.TExpressCompanyMapper;
import com.xinlian.biz.model.TExpressCompany;
import com.xinlian.common.enums.OperationModuleEnum;
import com.xinlian.common.enums.OperationTypeEnum;
import com.xinlian.common.request.OrderExportReq;
import com.xinlian.common.request.OrderSendReq;
import com.xinlian.common.request.QueryOrderListReq;
import com.xinlian.common.request.UpdateNewOrderReq;
import com.xinlian.common.response.QueryOrderListRes;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.util.Assert;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;


/**
 * cms
 * 购买订单相关接口
 */
@Slf4j
@Api(value = "购买商品相关接口")
@Controller
@RequestMapping("/newOrder")
public class NewOrderController {
    @Autowired
    private TNewOrderService tNewOrderService;
    @Autowired
    private TExpressCompanyMapper tExpressCompanyMapper;
    @Autowired
    private RedisClient redisClient;

    @ApiOperation("查询订单列表")
    @PostMapping("queryOrderList")
    @ResponseBody
    public ResponseResult queryOrderList(@RequestBody QueryOrderListReq queryOrderListReq) {
        QueryOrderListRes queryOrderListRes = tNewOrderService.queryOrderList(queryOrderListReq);
        return ResponseResult.ok(queryOrderListRes);
    }

    @ApiOperation("导出订单")
    @RequestMapping(value = "exportOrders",produces="application/octet-stream",method = POST)
    public ResponseResult exportOrders(@RequestBody OrderExportReq orderExportReq, HttpServletRequest request, HttpServletResponse response) {
        tNewOrderService.exportOrders(orderExportReq,request,response);
        return ResponseResult.ok();
    }

    //查询物流公司
    @ApiOperation("查询物流公司")
    @GetMapping("/queryExpressCompany")
    @ResponseBody
    public ResponseResult queryExpressCompany() {
        List<TExpressCompany> expressCompanies = tExpressCompanyMapper.selectList(new EntityWrapper<TExpressCompany>()
                .eq("status", 0));
        return ResponseResult.ok(expressCompanies);
    }

    //发货
    @ApiOperation("发货")
    @PostMapping("/deliver")
    @ResponseBody
    public ResponseResult deliver(@RequestBody OrderSendReq orderExportReq) {
        Assert.notNull(orderExportReq.getOrderId(), "订单号不能为空");
        Assert.notNull(orderExportReq.getExpressCode(), "快递单号不能为空");
        Assert.notNull(orderExportReq.getExpressCompanyComId(), "快递厂商标识不能为空");
        tNewOrderService.deliver(orderExportReq);
        return ResponseResult.ok();
    }

    //查看物流
    @ApiOperation("查看物流")
    @GetMapping("/queryExpress")
    @ResponseBody
    public ResponseResult queryExpress(@RequestParam Long orderId) {
        Assert.notNull(orderId, "订单号不能为空");
        JSONObject json = tNewOrderService.queryExpress(orderId);
        return ResponseResult.ok(json);
    }

    @OpeAnnotation(modelName = OperationModuleEnum.NEW_ORDER_MANAGE,typeName = OperationTypeEnum.OTHER_OPERATE,opeDesc = "修改订单收货相关信息")
    @ApiOperation(value = "修改订单收货相关信息",httpMethod = "POST")
    @PostMapping("/updateNewOrderById")
    @ResponseBody
    public ResponseResult updateNewOrderById(@RequestBody UpdateNewOrderReq req) {
        req.checkParam();
        tNewOrderService.updateNewOrderById(req);
        return ResponseResult.ok();
    }

    //设置活动开关
    @ApiOperation("设置活动开关 close  open")
    @GetMapping("/activeFlag")
    @ResponseBody
    public ResponseResult activeFlag(@RequestParam String flag) {
        Assert.notNull(flag, "标志不能为空");
        redisClient.set("BUY_GOODS_FLAG", flag);
        return ResponseResult.ok();
    }
    //设置活动开关
    @ApiOperation("查詢活動開關")
    @GetMapping("/queryActiveFlag")
    @ResponseBody
    public ResponseResult queryActiveFlag() {
        Object result = redisClient.get("BUY_GOODS_FLAG");
        JSONObject jsonObject = new JSONObject();
        if(result == null){
            jsonObject.put("flag", "close");
        }else{
            jsonObject.put("flag", result);
        }
        return ResponseResult.ok(jsonObject);
    }


    //设置活动开关
    @ApiOperation("设置活动图片开关 close  open")
    @GetMapping("/activeOpenFlag")
    @ResponseBody
    public ResponseResult activeOpenFlag(@RequestParam String flag) {
        Assert.notNull(flag, "标志不能为空");
        redisClient.set("BUY_GOODS_OPEN_FLAG", flag);
        return ResponseResult.ok();
    }

    //查詢活動图片開關
    @ApiOperation("查詢活動图片開關")
    @GetMapping("/queryActiveOpenFlag")
    @ResponseBody
    public ResponseResult queryActiveOpenFlag() {
        Object result = redisClient.get("BUY_GOODS_OPEN_FLAG");
        JSONObject jsonObject = new JSONObject();
        if(result == null){
            jsonObject.put("flag", "close");
        }else{
            jsonObject.put("flag", result);
        }
        return ResponseResult.ok(jsonObject);
    }


}
