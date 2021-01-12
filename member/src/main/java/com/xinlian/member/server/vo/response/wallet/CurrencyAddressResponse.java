package com.xinlian.member.server.vo.response.wallet;

import lombok.Data;

/**
 * @author Song
 * @date 2020-08-19 17:09
 * @description
 */
@Data
public class CurrencyAddressResponse {
    //之前地址
    private String basicAddress;
    //现在新增的trc20
    private String trcUsdtAddress;
}
