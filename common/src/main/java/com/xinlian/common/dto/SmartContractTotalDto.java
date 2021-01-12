package com.xinlian.common.dto;

import com.xinlian.common.response.SmartContractTotalRes;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SmartContractTotalDto {
    /**
     * 卖家总出金CAT
     */
    private BigDecimal sellerTotalOutAmount;
    /**
     * 卖家总入金USDT
     */
    private BigDecimal sellerTotalInAmount;
    /**
     * 买家总出金USDT
     */
    private BigDecimal buyerTotalOutAmount;
    /**
     * 买家总入金CAT
     */
    private BigDecimal buyerTotalInAmount;


    public SmartContractTotalRes smartContractTotalRes() {
        SmartContractTotalRes smartContractTotalRes = new SmartContractTotalRes();
        smartContractTotalRes.setSellerTotalOutAmount(sellerTotalOutAmount.setScale(4, BigDecimal.ROUND_HALF_UP).toString());
        smartContractTotalRes.setSellerTotalInAmount("+" + sellerTotalInAmount.setScale(4, BigDecimal.ROUND_HALF_UP));
        smartContractTotalRes.setBuyerTotalOutAmount(buyerTotalOutAmount.setScale(4, BigDecimal.ROUND_HALF_UP).toString());
        smartContractTotalRes.setBuyerTotalInAmount("+" + buyerTotalInAmount.setScale(4, BigDecimal.ROUND_HALF_UP));
        smartContractTotalRes.setCatMargin(sellerTotalOutAmount.abs().subtract(buyerTotalInAmount.abs()).setScale(4, BigDecimal.ROUND_HALF_UP).toString());
        smartContractTotalRes.setUsdtMargin(sellerTotalInAmount.abs().subtract(buyerTotalOutAmount.abs()).setScale(4, BigDecimal.ROUND_HALF_UP).toString());
        return smartContractTotalRes;
    }
}
