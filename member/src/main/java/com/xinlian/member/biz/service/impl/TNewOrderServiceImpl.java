package com.xinlian.member.biz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.xinlian.biz.dao.TNewOrderMapper;
import com.xinlian.biz.dao.TUserInfoMapper;
import com.xinlian.biz.model.TNewOrder;
import com.xinlian.biz.model.TUserAuth;
import com.xinlian.biz.model.TUserInfo;
import com.xinlian.biz.utils.AdminOptionsUtil;
import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
import com.xinlian.common.express.ExpressConfig;
import com.xinlian.common.request.BeforeSendReq;
import com.xinlian.common.request.OrderListReq;
import com.xinlian.common.request.OrderReq;
import com.xinlian.common.request.RegisterReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import com.xinlian.common.utils.DateFormatUtil;
import com.xinlian.member.biz.jwt.util.EncryptionUtil;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.service.IRegisterLoginService;
import com.xinlian.member.biz.service.TNewOrderService;
import com.xinlian.member.server.controller.handler.CheckSmsRuleHandler;
import com.xinlian.member.server.controller.handler.NewOrderHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.concurrent.locks.Lock;

import static com.xinlian.common.contants.OrderConstant.PAY_PASSWORD_ERROR;

@Slf4j
@Service
public class TNewOrderServiceImpl extends ServiceImpl<TNewOrderMapper, TNewOrder> implements TNewOrderService {
    @Autowired
    private NewOrderHandler newOrderHandler;
    @Autowired
    private RedisLockRegistry redisLockRegistry;
    @Autowired
    private TUserInfoMapper tUserInfoMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private CheckSmsRuleHandler checkSmsRuleHandler;
    @Autowired
    private IRegisterLoginService registerLoginService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ExpressConfig expressConfig;
    @Autowired
    private AdminOptionsUtil adminOptionsUtil;

    @Override
    public ResponseResult order(OrderReq orderReq) {
        log.info(DateFormatUtil.getByNowTime(7) + " 用户发起物品购买操作，请求参数：{}", JSONObject.toJSONString(orderReq));
        //判断当前是否开启购买
        String flag = redisClient.get("BUY_GOODS_FLAG");
        if(StringUtils.isBlank(flag) || StringUtils.equals("close", flag)){
            throw new BizException("暂未开放购买!");
        }
        //对操作加锁
        Lock lock = redisLockRegistry.obtain("BUY_GOODS_".concat(orderReq.getUid().toString()));
        boolean redisLockFlag = true;
        try{
            //尝试获取锁
            if(!lock.tryLock()){
                log.debug(Thread.currentThread().getName()+" : 请求购物下单接口，获取分布式锁失败!");
                redisLockFlag = false;
                throw new BizException("重复提交请稍后再试!");
            }
            //校验app端传来的常规参数合法性
            newOrderHandler.checkNullParam(orderReq);
            //校验用户状态、密码、验证码等基本信息
            TUserInfo userInfo = tUserInfoMapper.selectById(orderReq.getUid());
            String smsRedisKey = newOrderHandler.checkUserStatus(userInfo, orderReq);
            //校验节点以及购买总量 add 8.26日
            TUserAuth tUserAuth = newOrderHandler.checkNodeAndAmount(userInfo, orderReq);
            //校验通过后，进行下单
            boolean result = newOrderHandler.crateOrder(orderReq, userInfo);
            //下单成功后，删除验证码标志
            if(result){
                redisClient.increment("AUTH_BUY_TOTAL".concat(tUserAuth.getAuthSn()), orderReq.getAmount());
                redisClient.deleteByKey(smsRedisKey);
            }
            log.info("用户购买商品成功，请求参数：{}", JSONObject.toJSONString(orderReq));
            return new ResponseResult("下单成功");
        }catch (BizException e){
            log.error(DateFormatUtil.getByNowTime(7)+"-请求购物下单接口出现业务异常:{}",e.getMsg(),e);
            return new ResponseResult(e);
        }catch (Exception e){
            log.error(DateFormatUtil.getByNowTime(7)+"-请求购物下单接口出现系统异常:{}",e.toString(),e);
            return new ResponseResult(new BizException("请稍后重试!"));
        }finally {
            if(redisLockFlag) {
                lock.unlock();
                log.debug(Thread.currentThread().getName()+" : 请求购物下单接口，释放分布式锁success");
            }
        }
    }

    @Override
    public ResponseResult sendOrderCode(Long uid, Long nodeId, BeforeSendReq beforeSendReq) {
        //校验支付密码是否为空
        if(StringUtils.isBlank(beforeSendReq.getPassword())){
            throw new BizException("请先输入支付密码");
        }
        //查询当前用户
        TUserInfo userInfo = tUserInfoMapper.selectById(uid);
        String password = EncryptionUtil.md5Two(beforeSendReq.getPassword(), userInfo.getSalt());
        //校验密码正确性
        if (!password.equals(userInfo.getPayPassWord())) {
            throw new BizException(PAY_PASSWORD_ERROR);
        }
        RegisterReq registerReq = new RegisterReq();
        registerReq.setPhone(userInfo.getMobile());
        registerReq.setCountryCode(userInfo.getCountryCode());
        //校验短信是否超频
        checkSmsRuleHandler.doCheckSmsRuleHandler(registerReq.getPhone(), "_BUY_GOODS");
        registerReq.setType(16);// 设置为购物类型
        if (null == registerReq.getNodeId()) {
            registerReq.setNodeId(nodeId);
        }
        return registerLoginService.sendRegisterSms(registerReq, false);
    }

    @Override
    public ResponseResult list(Long uid, OrderListReq orderListReq) {
        Page<TNewOrder> page = this.selectPage(new Page<>(orderListReq.getPage(), orderListReq.getPageNum()),
                new EntityWrapper<TNewOrder>().eq("uid", uid).orderBy("create_time", false));
        return ResponseResult.ok(page.getRecords());
    }

    @Override
    public ResponseResult orderDetail(Long orderId) {
        JSONObject json = new JSONObject();
        //查询订单
        TNewOrder newOrder = this.selectById(orderId);
        if(newOrder == null){
            throw new BizException("订单不存在");
        }
        json.put("orderDetail", newOrder);
        //订单物流
        if(newOrder.getStatus() == 0){
            json.put("express", null);
        }else{
            //查询订单物流
            JSONObject express = getExpressData(newOrder.getExpressComId(), newOrder.getExpressCode());
            json.put("express", express);
        }
        return ResponseResult.ok(json);
    }

    @Override
    public ResponseResult goodsPrice() {
        String usdtAmountStr = adminOptionsUtil.findAdminOptionOne(AdminOptionsBelongsSystemCodeEnum
                .BUY_GOODS_MASK_MONEY_USDT.getBelongsSystemCode());
        if(StringUtils.isBlank(usdtAmountStr) || usdtAmountStr.contains("-")){
            throw new BizException("读取系统配置出错");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("price", usdtAmountStr);
        return ResponseResult.ok(jsonObject);
    }

    private JSONObject getExpressData(String expressId, String expressNo) {
        // 添加header
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "APPCODE ".concat(expressConfig.getAppCode()));
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, headers);
        // 添加参数
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(expressConfig.getQueryExpressUrl());
        builder.queryParam("comid", expressId);
        builder.queryParam("number", expressNo);
        final ResponseEntity<String> responseEntity = restTemplate
                .exchange(builder.build().toString(), HttpMethod.GET, request, String.class);
        JSONObject jsonObject = JSONObject.parseObject(responseEntity.getBody());
        if (jsonObject.containsKey("data")) {
            return jsonObject.getJSONObject("data");
        } else {
            return null;
        }
    }
}
