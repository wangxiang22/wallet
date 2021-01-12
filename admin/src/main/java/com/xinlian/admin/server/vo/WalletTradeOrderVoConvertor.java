package com.xinlian.admin.server.vo;

import com.xinlian.biz.model.TWalletTradeOrder;
import com.xinlian.common.Base.BaseVoConvertor;
import com.xinlian.common.enums.WalletTradeOrderStatusEnum;
import com.xinlian.common.utils.DateFormatUtil;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class WalletTradeOrderVoConvertor extends BaseVoConvertor<WalletTradeOrderVo, TWalletTradeOrder> {

    @Override
    public WalletTradeOrderVo convert(TWalletTradeOrder bo) throws Exception {
        WalletTradeOrderVo listVos = new WalletTradeOrderVo();
        try{
            listVos.setCurrencyCode(bo.getCurrencyCode());
            listVos.setCurrencyId(bo.getCurrencyId());
            listVos.setTradeStatus(bo.getTradeStatus());
            listVos.setTradeId(bo.getId());
            listVos.setTxId(bo.getTxId());
            listVos.setUserName(bo.getUserName());
            //状态判断-统一输出
            listVos.setTradeStatusName(WalletTradeOrderStatusEnum.getEnumDesc(bo.getTradeStatus()));
            listVos.setTradeCurrencyNum(bo.getTradeCurrencyNum());
            listVos.setTradeType(bo.getTradeType());
            listVos.setDes(bo.getDes());
            listVos.setDisposeCheckTime(DateFormatUtil.formatTillSecond(bo.getDisposeCheckTime()));
            listVos.setUid(bo.getUid());
            listVos.setCreateTime(DateFormatUtil.formatTillSecond(bo.getCreateTime()));
            listVos.setFailReason(bo.getFailReason());
            listVos.setTradeAddress(bo.getTradeAddress());
            listVos.setServerNodeId(bo.getServerNodeId());
            listVos.setServerNodeName(bo.getServerNodeName());
        }catch (Exception e){
            log.error("运营后台提币审核vo转换异常:{}", e.toString(), e);
        }
        return listVos;
    }

}
