package com.xinlian.member.server.vo;

import com.xinlian.biz.model.SimpleCurrencyTradeInfo;
import com.xinlian.common.Base.BaseVoConvertor;
import com.xinlian.common.enums.WalletTradeOrderStatusEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;


@Slf4j
public class WalletTradeOrderVoConvertor extends BaseVoConvertor<WalletTradeOrderVo, SimpleCurrencyTradeInfo> {

    @Override
    public WalletTradeOrderVo convert(SimpleCurrencyTradeInfo bo) throws Exception {
        WalletTradeOrderVo listVos = new WalletTradeOrderVo();
        try{
            listVos.setCreateTime(bo.getCreateTime());
            listVos.setCurrencyCode(bo.getCurrencyCode());
            listVos.setCurrencyId(bo.getCurrencyId());

            //状态判断-统一输出
            if(getTradeOrderMap().containsKey(bo.getDes())){
                listVos.setTradeStatus(WalletTradeOrderStatusEnum.TRADE_SUCCESS.getCode());
                listVos.setTradeStatusName(WalletTradeOrderStatusEnum.TRADE_SUCCESS.getDesc());
            }else {
                listVos.setTradeStatusName(WalletTradeOrderStatusEnum.getAppShowEnumDesc(bo.getTradeStatus()));
                listVos.setTradeStatus(bo.getTradeStatus());
            }
            listVos.setTradeCurrencyNum(bo.getTradeCurrencyNum());
            listVos.setTradeType(bo.getTradeType());
            listVos.setDes(bo.getDes());
            listVos.setWalletId(bo.getWalletId());
            listVos.setTradeId(bo.getTradeId());
            listVos.setUid(bo.getUid());
            listVos.setBalanceNum(bo.getBalanceNum());
        }catch (Exception e){
            log.error("某币种下交易流水vo转换异常:{}", e.toString(), e);
        }
        return listVos;
    }

    private Map<String,String> getTradeOrderMap(){
        Map<String,String> map = new HashMap<String,String>();
        map.put("ceo购买","");
        map.put("ceo购入cat","");
        map.put("Refusal to return","");
        map.put("rocket转入","");
        map.put("充值","");
        map.put("充值到账","");
        map.put("公链手动确认","");
        map.put("内部转账","");
        map.put("后台充值","");
        map.put("多充扣款","");
        map.put("多转扣款","");
        map.put("抽奖所得","");
        map.put("抽宝箱","");
        map.put("拒绝回款","");
        map.put("提现-内部转账","");
        map.put("提现冻结","");
        map.put("提现拒绝返回","");
        map.put("激活矿机","");
        map.put("矿机激活费用退款","");
        map.put("空投发放","");
        map.put("算能设备兑换","");
        map.put("转出","");
        map.put("转出到rocket","");
        return map;
    }
}
