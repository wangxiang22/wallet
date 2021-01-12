package com.xinlian.member.server.controller.shoppingMall;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.common.request.BeforeSendReq;
import com.xinlian.common.request.OrderListReq;
import com.xinlian.common.request.OrderReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.jwt.annotate.EncryptionH5Annotation;
import com.xinlian.member.biz.jwt.util.JwtUtil;
import com.xinlian.member.biz.service.TNewOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * cms
 * 购买订单相关接口
 */
@Api(value = "购买商品相关接口")
@RestController
@RequestMapping("/newOrder")
public class NewOrderController {
    @Autowired
    private TNewOrderService tNewOrderService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private HttpServletRequest httpServletRequest;

    @ApiOperation("发送验证码")
    @PostMapping("/sendOrderCode")
    @EncryptionH5Annotation
    public ResponseResult sendOrderCode(@RequestBody Map<String, String> paramMap) {
        //封装参数
        BeforeSendReq beforeSendReq = JSONObject.parseObject(paramMap.get("data"), BeforeSendReq.class);
        Long uid = jwtUtil.getUserId(httpServletRequest);
        Long nodeId = jwtUtil.getNodeId(httpServletRequest);
        return tNewOrderService.sendOrderCode(uid, nodeId, beforeSendReq);
    }

    @ApiOperation("获得商品价格")
    @GetMapping("/goodsPrice")
    public ResponseResult goodsPrice() {
        return tNewOrderService.goodsPrice();
    }

    @ApiOperation("下单")
    @PostMapping("/order")
    @EncryptionH5Annotation
    public ResponseResult order(@RequestBody Map<String, String> paramMap){
        //封装参数
        OrderReq orderReq = JSONObject.parseObject(paramMap.get("data"), OrderReq.class);
        orderReq.setUid(jwtUtil.getUserId(httpServletRequest));
        return tNewOrderService.order(orderReq);
    }

    @ApiOperation("订单列表")
    @PostMapping("/list")
    public ResponseResult list(@RequestBody OrderListReq orderListReq) {
        Long uid = jwtUtil.getUserId(httpServletRequest);
        return tNewOrderService.list(uid, orderListReq);
    }

    @ApiOperation("订单详情")
    @GetMapping("/orderDetail")
    public ResponseResult orderDetail(@RequestParam(value = "orderId") Long orderId) {
        return tNewOrderService.orderDetail(orderId);
    }

}
