package com.xinlian.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CatWalletPayCallbackErrorRes implements Serializable {
    private static final long serialVersionUID = 1L;

    // 一般情况下，25小时以内完成8次通知（通知的间隔频率一般是：4m,10m,10m,1h,2h,6h,15h）；
    private Integer repeateNum;

    //回调地址
    private String callback;

    //回调时间（当前时间大于此时间即回调）
    private Long callbackTime;

    //订单号
    private String orderNo;

    // 回调结果
    private CatWalletPayCallbackRes catWalletPayCallbackRes;

}
