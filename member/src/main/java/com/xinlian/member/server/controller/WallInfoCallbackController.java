package com.xinlian.member.server.controller;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.common.enums.RechargeOperTypeEnum;
import com.xinlian.common.result.BizException;
import com.xinlian.common.result.ErrorInfoEnum;
import com.xinlian.member.biz.jwt.annotate.PassToken;
import com.xinlian.member.biz.udun.UdunConstant;
import com.xinlian.member.server.controller.handler.MaleChainCallbackHandler;
import com.xinlian.member.server.controller.handler.MaleChainWithdrawHandler;
import com.xinlian.member.server.controller.handler.MaleSearchResultHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 资产钱包相关控制层
 */
@RestController
@RequestMapping(value = "/system")
@Api(value = "资产钱包回调相关接口")
@Slf4j
public class WallInfoCallbackController {

    @Autowired
    private MaleChainCallbackHandler maleChainCallbackHandler;
    @Autowired
    private MaleChainWithdrawHandler maleChainWithdrawHandler;
    @Autowired
    private MaleSearchResultHandler maleSearchResultHandler;

    /**
     * maleChain提币回调接口
     *
     * @return
     */
    @ApiOperation("maleChain提币回调接口")
    @PassToken
    @PostMapping(value="/v1/maleChain/callback/withdraw")
    public String maleChainRecharge(@RequestBody JSONObject jsonObject) {
        String data = "";
        // 校验签名
        try {
            data = maleChainWithdrawHandler.decryptByPrivateKey(jsonObject.getString("data"));
            log.info("校验签名成功");
        } catch (Exception e) {
            log.warn("解密失败回调数据，{}", jsonObject.toJSONString());
            throw new BizException(ErrorInfoEnum.UDUN_CALLBACK_ERROR);
        }
        try {
            log.info("maleChainRechargeCallBackRequest : " + data);
            String resultStr = maleChainCallbackHandler.doWithdrawCallbackMethod(data);
            if(UdunConstant.MAIL_CHAIN_OK.equalsIgnoreCase(resultStr)){return UdunConstant.MAIL_CHAIN_OK;}
            else{ return UdunConstant.MAIL_CHAIN_ERROR;}
        }catch (BizException e){
            log.error(e.getMessage(),e);
            log.warn("提币回调接口处理业务出现异常：{}", data);
            return UdunConstant.MAIL_CHAIN_ERROR;
        }catch (Exception e){
            log.error(e.getMessage(),e);
            log.warn("提币回调JSON：{}解析失败", data);
            return UdunConstant.MAIL_CHAIN_ERROR;
        }
    }

    @ApiOperation("maleChain充值回调接口")
    @PassToken
    @PostMapping(value="/v1/maleChain/callback/recharge")
    public String coinChargingCallBack(@RequestBody JSONObject jsonObject) {
        String data = "";
        // 校验签名
        try {
            data = maleChainWithdrawHandler.decryptByPrivateKey(jsonObject.getString("data"));
            log.info("rsa解密成功");
        } catch (Exception e) {
            log.warn("解密失败回调数据，{}", jsonObject.toJSONString());
            throw new BizException(ErrorInfoEnum.UDUN_CALLBACK_ERROR);
        }
        try {
            log.info("maleChain充值回调接口参数 : " + data);
            return maleChainCallbackHandler.doRechargeCallbackMethod(data, RechargeOperTypeEnum.MALE_CHAIN_RECHARGE.getOperType());
        }catch (BizException e){
            log.error(e.getMessage(),e);
            log.warn("充值回调接口处理业务出现异常：{}", data);
            return UdunConstant.MAIL_CHAIN_ERROR;
        }catch (Exception e){
            log.error(e.getMessage(),e);
            log.warn("充值回调JSON：{}解析失败", data);
            return UdunConstant.MAIL_CHAIN_ERROR;
        }
    }

    @ApiOperation("maleChain-检测之前U盾地址-充值回调接口")
    @PassToken
    @PostMapping(value="/v1/udun/callback/recharge")
    public String udunCurrencyAddressChargingCallBack(@RequestBody JSONObject jsonObject) {
        String data = "";
        // 校验签名
        try {
            data = maleChainWithdrawHandler.decryptByPrivateKey(jsonObject.getString("data"));
            log.info("u盾检测地址充值回调rsa解密成功");
        } catch (Exception e) {
            log.warn("u盾检测地址充值回调解密失败，{}", jsonObject.toJSONString());
            throw new BizException(ErrorInfoEnum.UDUN_CALLBACK_ERROR);
        }
        try {
            log.info("u盾检测地址充值回调接口参数 : " + data);
            return maleChainCallbackHandler.doRechargeCallbackMethod(data,RechargeOperTypeEnum.UDUN_RECHARGE.getOperType());
        }catch (BizException e){
            log.error(e.getMessage(),e);
            log.warn("u盾检测地址充值回调出现异常：{}", data);
            return UdunConstant.MAIL_CHAIN_ERROR;
        }catch (Exception e){
            log.error(e.getMessage(),e);
            log.warn("u盾检测地址充值回调JSON：{}解析失败", data);
            return UdunConstant.MAIL_CHAIN_ERROR;
        }
    }

    @ApiOperation("根据交易hash补偿一直没有回调回来的数据")
    @PassToken
    @PostMapping(value="/v1/maleChain/searchResult")
    public String maleChainSearchResult (
            @ApiParam(name = "jsonObject", value = "type:(充值:recharge, 提现:extract),tx_hash:交易哈希,auth:请求凭证,如：{\"type\":\"recharge\",\"tx_hash\":\"\",\"auth\":\"\"}", required = true)
            @RequestBody JSONObject jsonObject) {
        try {
            // 校验此接口是否有对应权限
            boolean checkAuthFlag = "auth!2020".equals(jsonObject.getString("auth"));
            if (!checkAuthFlag){ throw new BizException("无权限");}
            return  maleSearchResultHandler.searchResultByTxHash(jsonObject);
        } catch (BizException e){
            return UdunConstant.MAIL_CHAIN_ERROR+e.getMsg();
        } catch (Exception e) {
            log.warn("补偿数据异常，{}", jsonObject.toJSONString());
            return UdunConstant.MAIL_CHAIN_ERROR;
        }
    }

    @ApiOperation("查询有余额的地址")
    @PassToken
    @PostMapping(value="/v1/maleChain/haveBalanceAddress")
    public String haveBalanceAddress (
            @ApiParam(name = "jsonObject", value = "auth:请求凭证,如：{\"auth\":\"\"}", required = true)
            @RequestBody JSONObject jsonObject) {
        try {
            // 校验此接口是否有对应权限
            boolean checkAuthFlag = "auth!2020".equals(jsonObject.getString("auth"));
            if (!checkAuthFlag){ throw new BizException("无权限");}
            return  maleSearchResultHandler.haveBalanceAddress();
        } catch (BizException e){
            return UdunConstant.MAIL_CHAIN_ERROR+e.getMsg();
        } catch (Exception e) {
            log.warn("查询有余额的地址异常，{}", jsonObject.toJSONString());
            return UdunConstant.MAIL_CHAIN_ERROR;
        }
    }
}
