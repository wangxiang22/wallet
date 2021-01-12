package com.xinlian.admin.server.vo.response;

import lombok.Data;

/**
 * com.xinlian.admin.server.vo.response
 *
 * @date 2020/2/17 11:38
 */
@Data
public class UsdtSoldPriceDataResponse {
    //cat总出金
    private String catTotalOutAmount;
    //usdt总入金
    private String usdtTotalInAmount;
    //总均价
    private String totalAvgPrice;


}
