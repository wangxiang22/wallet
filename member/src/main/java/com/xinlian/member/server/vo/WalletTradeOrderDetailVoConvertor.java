package com.xinlian.member.server.vo;

import com.xinlian.biz.model.TWalletTradeOrder;
import com.xinlian.biz.utils.ConstantUtils;
import com.xinlian.common.Base.BaseVoConvertor;
import com.xinlian.common.enums.WalletTradeOrderStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;


@Slf4j
public class WalletTradeOrderDetailVoConvertor extends BaseVoConvertor<WalletTradeOrderDetailVo, TWalletTradeOrder> {

    @Override
    public WalletTradeOrderDetailVo convert(TWalletTradeOrder bo) throws Exception {
        WalletTradeOrderDetailVo vo = new WalletTradeOrderDetailVo();
        try{
            vo.setCreateTime(bo.getCreateTime());
            vo.setCurrencyCode(bo.getCurrencyCode());
            vo.setCurrencyId(bo.getCurrencyId());

            //状态判断-统一输出
            if(ConstantUtils.getTradeOrderMap().containsKey(bo.getDes())){
                vo.setTradeStatus(WalletTradeOrderStatusEnum.TRADE_SUCCESS.getCode());
                vo.setTradeStatusName(WalletTradeOrderStatusEnum.TRADE_SUCCESS.getDesc());
            }else {
                vo.setTradeStatusName(WalletTradeOrderStatusEnum.getAppShowEnumDesc(bo.getTradeStatus()));
                vo.setTradeStatus(bo.getTradeStatus());
            }
            vo.setTradeCurrencyNum(bo.getTradeCurrencyNum());
            vo.setTradeAddress(bo.getTradeAddress());
            vo.setTradeType(bo.getTradeType());
            vo.setDes(bo.getDes());
            vo.setTxId("--");
            if(StringUtils.isNotEmpty(bo.getTxId())){
                vo.setTxId(bo.getTxId());
            }
            vo.setFailReason(vo.getFailReason());
            vo.setId(bo.getId());
            vo.setCounterPartyMobile(bo.getCounterPartyMobile());
            vo.setCounterPartyUserName(bo.getCounterPartyUserName());

        }catch (Exception e){
            log.error("钱包交易流水详情vo转换异常:{}", e.toString(), e);
        }
        return vo;
    }



}
