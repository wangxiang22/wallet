package com.xinlian.biz.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * com.xinlian.biz.utils
 *
 * @author by Song
 * @date 2020/2/17 20:38
 */
public class ConstantUtils {

    /**
     * 之前交易流水desc
     * @return
     */
    public static Map<String,String> getTradeOrderMap(){
        Map<String,String> map = new HashMap<String,String>();
        map.put("ceo购买","1");
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
