package com.xinlian.member.server.controller;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.common.result.BizException;
import com.xinlian.common.result.ErrorInfoEnum;
import com.xinlian.member.biz.jwt.annotate.PassToken;
import com.xinlian.member.biz.udun.UdunConstant;
import com.xinlian.member.server.controller.handler.TrcUsdtRechargeHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 无名氏
 * @date 2020-08-18 13:48
 * @description trc-usdt -回调相关接口
 */
@RestController
@RequestMapping(value = "/{versionPath}/system")
@Api(value = "trc-USDT协议资产钱包回调相关接口")
@Slf4j
public class TrcUsdtCallbackController {

    @Autowired
    private TrcUsdtRechargeHandler trcUsdtRechargeHandler;

    @ApiOperation("trcUSDT充值回调接口")
    @PassToken
    @PostMapping(value="/v1/trc/recharge/callback")
    public String trcChargingCallBack(@RequestBody JSONObject jsonObject) {
        String data = "";
        // 校验签名
        try {
            //check ip rule
            trcUsdtRechargeHandler.checkTrcRequestIp();
            data = trcUsdtRechargeHandler.decryptByPrivateKey(jsonObject.getString("data"));
            log.info("TRC_rsa解密成功");
        } catch (Exception e) {
            log.warn("TRC解密失败回调数据，{}", jsonObject.toJSONString());
            throw new BizException(ErrorInfoEnum.UDUN_CALLBACK_ERROR);
        }
        try {
            log.info("TRC充值回调接口参数 : " + data);
            return trcUsdtRechargeHandler.doRechargeCallbackMethod(data);
        }catch (BizException e){
            log.error(e.getMessage(),e);
            log.warn("TRC充值回调接口处理业务出现异常：{}", data);
            return UdunConstant.MAIL_CHAIN_ERROR;
        }catch (Exception e){
            log.error(e.getMessage(),e);
            log.warn("TRC充值回调JSON：{}解析失败", data);
            return UdunConstant.MAIL_CHAIN_ERROR;
        }
    }

    @ApiOperation("trcUSDT充值补偿查询接口")
    @PassToken
    @PostMapping(value="/v1/trc/recharge/compensateQuery")
    public String rechargeCompensateQuery(@RequestBody JSONObject jsonObject) {
        try {
            // 校验此接口是否有对应权限
            boolean checkAuthFlag = "auth!TRC2020".equals(jsonObject.getString("auth"));
            if (!checkAuthFlag){ throw new BizException("无权限");}
            return  trcUsdtRechargeHandler.rechargeCompensateQuery(jsonObject);
        } catch (BizException e){
            return UdunConstant.MAIL_CHAIN_ERROR+e.getMsg();
        } catch (Exception e) {
            log.warn("补偿数据异常，{}", jsonObject.toJSONString());
            return UdunConstant.MAIL_CHAIN_ERROR;
        }
    }
}
