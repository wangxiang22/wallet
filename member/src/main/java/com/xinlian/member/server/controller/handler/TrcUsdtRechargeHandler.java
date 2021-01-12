package com.xinlian.member.server.controller.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xinlian.biz.dao.TWalletInfoMapper;
import com.xinlian.biz.dao.TWalletTradeOrderMapper;
import com.xinlian.biz.dao.TrcWalletInfoMapper;
import com.xinlian.biz.model.TWalletInfo;
import com.xinlian.biz.model.TWalletTradeOrder;
import com.xinlian.biz.model.TrcWalletInfoModel;
import com.xinlian.biz.utils.AdminOptionsUtil;
import com.xinlian.common.enums.*;
import com.xinlian.common.result.BizException;
import com.xinlian.common.result.ErrorInfoEnum;
import com.xinlian.common.utils.*;
import com.xinlian.member.biz.service.CallInterfaceService;
import com.xinlian.member.biz.service.TWalletTradeOrderService;
import com.xinlian.member.biz.trcusdt.TrcUsdtConfig;
import com.xinlian.member.biz.trcusdt.vo.request.RechargeCompensateQueryRequest;
import com.xinlian.member.biz.trcusdt.vo.request.TrcUsdtRechargeRequest;
import com.xinlian.member.biz.udun.UdunConstant;
import com.xinlian.member.biz.udun.aoplog.UdunLogAnnotation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 无名氏
 * @date 2020-08-18 15:36
 * @description
 */
@Slf4j
@Component
public class TrcUsdtRechargeHandler {

    @Autowired
    private TrcUsdtConfig trcUsdtConfig;
    @Autowired
    private TWalletTradeOrderMapper walletTradeOrderMapper;
    @Autowired
    private TWalletInfoMapper walletInfoMapper;
    @Autowired
    private TWalletTradeOrderService walletTradeOrderService;
    @Autowired
    private CallInterfaceService callInterfaceService;
    @Autowired
    private TrcWalletInfoMapper trcWalletInfoMapper;
    @Autowired
    private AdminOptionsUtil adminOptionsUtil;
    @Autowired
    private HttpServletRequest httpServletRequest;
    /**
     * 公钥解密
     * @param encryptData 待解密文
     * @return
     * @throws Exception
     */
    public String decryptByPrivateKey(String encryptData)throws Exception{
        if(StringUtils.isEmpty(encryptData)){
            log.error("trc提币接口 - 无data");
            throw new BizException("无data值");
        }
        byte [] decryptByte = RSAEncrypt.decryptByPrivateKey(Base64Utils.decode(encryptData),trcUsdtConfig.getPrivateKey());
        return new String(decryptByte);
    }

    /**
     * trc-充值回调
     * @param data
     * @return
     */
    @UdunLogAnnotation(udunOpeType = "TRC充值回调接口")
    @Transactional
    public String doRechargeCallbackMethod(String data) {
        log.info("收到充币成功回调[{}]", data);
        TrcUsdtRechargeRequest trcUsdtRechargeRequest = JSON.parseObject(data, TrcUsdtRechargeRequest.class);
        //充币回调
        //验证是否重复推送过充值，推个充值，也返回OK
        if(this.checkChargeMoneyTradeRecord(trcUsdtRechargeRequest.getAddress(),trcUsdtRechargeRequest.getTxid())){
            return UdunConstant.MAIL_CHAIN_OK;
        }
        //检验推送的数值大小
        if (trcUsdtRechargeRequest.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("地址：{}充币（币种：{},主币种：{}）数量({})小于等于0，系统不允许录入数据"
                    ,trcUsdtRechargeRequest.getAddress(),"","",trcUsdtRechargeRequest.getValue().toPlainString());
            throw new BizException(ErrorInfoEnum.DEPOSIT_AMOUNT_ERROR);
        }
        //检测充值地址是否存在对应uid
        TrcWalletInfoModel trcWalletInfoModel = this.getTrcWalletInfoByChargeAddress(trcUsdtRechargeRequest.getAddress());
        if(null==trcWalletInfoModel){throw new BizException(ErrorInfoEnum.CALL_BACK_WALLET_INFO_ERROR);}
        //1.写入交易记录表
        int tradeResultNum = this.buildWalletTradeOrderBasicModel(trcUsdtRechargeRequest,trcWalletInfoModel);
        //2.计入客户钱包表
        TWalletInfo updateWallet = this.createRechargeWalletInfo(trcWalletInfoModel,trcUsdtRechargeRequest.getValue());
        //更新钱包数值
        int wallInfoResultNum = walletInfoMapper.updateModelByTrcRecharge(updateWallet);
        if(tradeResultNum==0||wallInfoResultNum==0){
            throw new BizException(ErrorInfoEnum.DEPOSIT_AMOUNT_INSERT_ERROR);
        }
        //进行极光推送
        walletTradeOrderService.pushJiGuangNotice(updateWallet,true,trcUsdtRechargeRequest.getValue());
        return UdunConstant.MAIL_CHAIN_OK;
    }

    private TWalletInfo createRechargeWalletInfo(TrcWalletInfoModel trcWalletInfoModel, BigDecimal amount){
        TWalletInfo updateWallet = new TWalletInfo();
        updateWallet.setUid(trcWalletInfoModel.getUid());
        updateWallet.setCurrencyId(trcWalletInfoModel.getCurrencyId());
        updateWallet.setCurrencyCode(trcWalletInfoModel.getCurrencyCode());
        updateWallet.setMovableAssetsNum(amount);
        return updateWallet;
    }

    private Integer buildWalletTradeOrderBasicModel(TrcUsdtRechargeRequest trcUsdtRechargeRequest, TrcWalletInfoModel trcWalletInfoModel){
        TWalletTradeOrder walletTradeOrder = new TWalletTradeOrder();
        walletTradeOrder.setTradeStatus(WalletTradeOrderStatusEnum.TRADE_SUCCESS.getCode());
        walletTradeOrder.setTxId(trcUsdtRechargeRequest.getTxid());
        walletTradeOrder.setTradeType(WalletTradeTypeEnum.TOP_UP.getTradeType());
        walletTradeOrder.setDes(WalletTradeTypeEnum.TOP_UP.getTradeDesc());
        walletTradeOrder.setCurrencyId(trcWalletInfoModel.getCurrencyId());
        walletTradeOrder.setCurrencyCode(trcWalletInfoModel.getCurrencyCode());
        walletTradeOrder.setTradeAddress(trcUsdtRechargeRequest.getAddress());
        walletTradeOrder.setTradeCurrencyNum(trcUsdtRechargeRequest.getValue());
        walletTradeOrder.setTradeSystemCode(WalletTradeSystemCodeEnum.APP_TRADE_TRC.getCode());
        //矿工费收取的是主币eth  eth的token就用固定精度18取处理
        walletTradeOrder.setMinersFee(UdunBigDecimalUtil.disposeValueDecimal(trcUsdtRechargeRequest.getFee(), UdunConstant.MINERS_DECIMALS));
        //根据回调交易地址找到
        walletTradeOrder.setUid(trcWalletInfoModel.getUid());
        walletTradeOrder.setCreateTime(new Date());
        return walletTradeOrderMapper.saveModel(walletTradeOrder);
    }

    private TrcWalletInfoModel getTrcWalletInfoByChargeAddress(String address) {
        TrcWalletInfoModel whereModel = new TrcWalletInfoModel();
        whereModel.setCurrencyAddress(address);
        whereModel.setCurrencyCode(CurrencyEnum.USDT.getCurrencyCode());
        TrcWalletInfoModel getModel = trcWalletInfoMapper.getTrcWalletInfo(whereModel);
        return getModel;
    }

    private boolean checkChargeMoneyTradeRecord(String address,String txId){
        TWalletTradeOrder whereModel = new TWalletTradeOrder();
        whereModel.setTxId(txId);
        whereModel.setTradeAddress(address);
        TWalletTradeOrder walletTradeOrder = walletTradeOrderMapper.getByCriteria(whereModel);
        if (null!=walletTradeOrder){
            log.info("重复[{}]推送 txid:[{}]","充币回调",txId);
            return true;
        }
        return false;
    }

    /**
     * 充值回调补偿接口
     * @param jsonObject
     * @return
     */
    public String rechargeCompensateQuery(JSONObject jsonObject)throws Exception {
        RechargeCompensateQueryRequest rechargeCompensateQueryRequest = this.convertToSearchRequestRequest(jsonObject);
        JSONObject responseObj = this.doRequestUrl(rechargeCompensateQueryRequest);
        return responseObj.toJSONString();
    }

    public JSONObject doRequestUrl(RechargeCompensateQueryRequest searchResultRequest) throws Exception{
        JSONObject requestObj = new JSONObject();
        String toUrl = trcUsdtConfig.getGatewayHost() + trcUsdtConfig.getRechargeCompensateSearch();
        JSONObject param = (JSONObject) JSON.toJSON(searchResultRequest);
        log.debug("maleChain结果补偿请求参数：{}",param.toJSONString());
        byte [] requestParamByte = RSAEncrypt.encryptByPrivateKey(param.toJSONString().getBytes(),trcUsdtConfig.getPrivateKey());
        requestObj.put("data", Base64Utils.encode(requestParamByte));
        JSONObject responseObj = callInterfaceService.callInterface(requestObj.toJSONString(),toUrl,JSONObject.class);
        return responseObj;
    }

    private RechargeCompensateQueryRequest convertToSearchRequestRequest(JSONObject jsonObject) {
        RechargeCompensateQueryRequest request = new RechargeCompensateQueryRequest();
        request.setBlocknumber(jsonObject.getLong("blocknumber"));
        return request;
    }

    public void checkTrcRequestIp() {
        String requestEnableIpList = adminOptionsUtil.findAdminOptionOne(AdminOptionsBelongsSystemCodeEnum.TRC_REQUEST_ENABLE_IP_LIST.getBelongsSystemCode());
        if("-1".equals(requestEnableIpList) || null==requestEnableIpList){
            return;
        }
        String ip = SystemUtils.getIpAddress(httpServletRequest);
        log.info(DateFormatUtil.getByNowTime(7)+"请求【RECHARGE_TRC_IP】地址:{}",ip);
        if(!requestEnableIpList.contains(ip)){
            throw new BizException("非法IP地址请求!");
        }
    }
}
