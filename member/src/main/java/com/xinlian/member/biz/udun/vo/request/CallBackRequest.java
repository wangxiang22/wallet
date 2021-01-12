package com.xinlian.member.biz.udun.vo.request;

import lombok.Data;

@Data
public class CallBackRequest {

    /**
     * 到账地址：注意是到账地址
     */
    private String address;

    /**
     * 交易金额，注意这里的金额是没有处理精度的，集合decimals字段进行精度处理
     */
    private String amount;

    /**
     * 区块高度
     */
    private String blockHigh;

    /**
     * 业务id :提币回调才有（是当时我们发送提币的时候，传的businessID）
     *        充币回调没有该数据
     */
    private String businessId;

    /**
     * 币种类型
     */
    private String coinType;

    /**
     * 主币种类型
     */
    private String mainCoinType;

    /**
     * 精度
     */
    private String decimals;

    /**
     * 旷工费
     */
    private String fee;

    /**
     * 备注：特殊币种有用到
     */
    private String memo;

    /**
     * 状态 见 CallBackBodyStatusEnum 这个枚举解释
     */
    private Integer status;

    /**
     * 交易id
     */
    private String tradeId;

    /**
     * 交易类型：1 提币 2 充币
     */
    private Integer tradeType;

    /**
     * txid号
     */
    private String txId;
}
