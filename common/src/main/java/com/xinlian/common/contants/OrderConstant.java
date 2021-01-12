package com.xinlian.common.contants;

public interface OrderConstant {
    String PAY_PASSWORD_ERROR= "支付密码错误";
    String BALANCE_NOT_ENOUGH="余额不足";
    String FEE_NOT_ENOUGH="手续费不足";
    String ORDER_IS_ONLINE = "您已有挂单，请等待成交后重新挂单";
    String MAKE_ORDER_ERROR= "挂单失败请重试";
    String CAT_FROZEN="CAT挂单冻结";
    String CAG_FROZEN="挂单手续费-CAG冻结";
    String ORDER_TIME_OUT= "订单已过期";
    String ORDER_IS_TRADED = "订单已经成交过了";
    String ORDER_ERROR="交易失败请重试";
    String BY_CAT= "卖出CAT所得";
    String BUY_CAT="买入CAT";
    String CAG_FEE="兑入CAT手续费";
    String ORDER_NO_PROMISSION="您没有资格购入该订单(地址与卖家设定不匹配)";
    String TIME_OUT_BACK="挂单时间过期退还";
    String PRICE_OR_AMOUNT_OR_ADDR_NO="请填写完整信息后提交";
    String NOT_IN_SPOT_TIME="当前时间不在挂单交易时间内";
    String WALLET_ERROR="钱包信息异常,请稍后重试";
    String ORDER_IS_MISSING="订单已不存在";
}
