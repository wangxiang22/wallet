package com.xinlian.admin.server.vo;

import com.xinlian.biz.model.TWalletTradeOrder;
import com.xinlian.biz.utils.ConstantUtils;
import com.xinlian.common.Base.BaseVoConvertor;
import com.xinlian.common.enums.WalletTradeOrderStatusEnum;
import com.xinlian.common.utils.DateFormatUtil;
import com.xinlian.common.utils.PrStringUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * com.xinlian.admin.server.vo
 *
 * @author by Song
 * @date 2020/2/17 20:29
 */
@Slf4j
public class WalletTradeFlowVoConvertor extends BaseVoConvertor<WalletTradeFlowVo,TWalletTradeOrder> {

    @Override
    public WalletTradeFlowVo convert(TWalletTradeOrder bo) throws Exception {
        WalletTradeFlowVo vo = new WalletTradeFlowVo();
        try{
            vo.setCurrencyCode(bo.getCurrencyCode());
            vo.setCurrencyId(bo.getCurrencyId());
            vo.setTradeId(bo.getId());
            vo.setTxId(bo.getTxId());
            vo.setUserName(bo.getUserName());
            //状态判断-统一输出
            if(ConstantUtils.getTradeOrderMap().containsKey(bo.getDes())){
                log.info("--走之前的交易流水");
                vo.setTradeStatus(WalletTradeOrderStatusEnum.TRADE_SUCCESS.getCode());
                vo.setTradeStatusName(WalletTradeOrderStatusEnum.TRADE_SUCCESS.getDesc());
            }else {
                vo.setTradeStatusName(WalletTradeOrderStatusEnum.getAppShowEnumDesc(bo.getTradeStatus()));
                vo.setTradeStatus(bo.getTradeStatus());
            }
            vo.setTradeCurrencyNum(bo.getTradeCurrencyNum());
            vo.setTradeType(bo.getTradeType());
            vo.setDes(bo.getDes());
            vo.setUid(bo.getUid());
            vo.setCreateTime(DateFormatUtil.formatTillSecond(bo.getCreateTime()));
            vo.setFailReason(bo.getFailReason());
            vo.setTradeAddress(bo.getTradeAddress());
            vo.setMinersFee(PrStringUtils.fmtNumToString(bo.getMinersFee(),"0"));
            vo.setTradeFee(PrStringUtils.fmtNumToString(bo.getTradeFee(),"0"));
            vo.setServerNodeId(bo.getServerNodeId());
            vo.setServerNodeName(bo.getServerNodeName());
            vo.setRemark(bo.getRemark());
            vo.setCounterPartyUid(bo.getCounterPartyUid());
        }catch (Exception e){
            log.error("运营后台交易流水vo转换异常:{}", e.toString(), e);
        }
        return vo;
    }
}
