package com.xinlian.admin.server.vo;

import com.xinlian.biz.model.TWalletTradeOrder;
import com.xinlian.common.Base.BaseVoConvertor;
import com.xinlian.common.enums.WalletTradeOrderStatusEnum;
import com.xinlian.common.utils.DateFormatUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @see com.xinlian.common.enums.WalletTradeSystemCodeEnum#ADMIN_TRADE
 * 钱包Admin充值
 */
@Slf4j
public class WalletTradeRechargeVoConvertor  extends BaseVoConvertor<WalletTradeRechargeVo, TWalletTradeOrder> {

    @Override
    public WalletTradeRechargeVo convert(TWalletTradeOrder bo) throws Exception {
        WalletTradeRechargeVo listVos = new WalletTradeRechargeVo();
        try{
            listVos.setCurrencyCode(bo.getCurrencyCode());
            listVos.setCurrencyId(bo.getCurrencyId());
            listVos.setTradeStatus(bo.getTradeStatus());
            listVos.setTradeId(bo.getId());
            listVos.setUserName(bo.getUserName());
            listVos.setDisposeCheckTime(DateFormatUtil.formatTillSecond(bo.getDisposeCheckTime()));
            listVos.setUid(bo.getUid());
            //状态判断-统一输出
            listVos.setTradeStatusName(WalletTradeOrderStatusEnum.getEnumDesc(bo.getTradeStatus()));
            listVos.setTradeCurrencyNum(bo.getTradeCurrencyNum());
            listVos.setTradeAddress(bo.getTradeAddress());
            listVos.setTradeType(bo.getTradeType());
            listVos.setDes(bo.getDes());
            listVos.setServerNodeId(bo.getServerNodeId());
            listVos.setServerNodeName(bo.getServerNodeName());
            listVos.setRemark(bo.getRemark());
        }catch (Exception e){
            log.error("运营后台充值列表vo转换异常:{}", e.toString(), e);
        }
        return listVos;
    }
}
