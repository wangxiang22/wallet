package com.xinlian.biz.dao;

import com.xinlian.biz.model.CurrencyBalanceHourChangeModel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

@Component
public interface CurrencyBalanceHourChangeMapper {

    /**
     * 统计币种余额到新表中
     */
    Integer statisticsCurrencyBalance(@Param(value = "currencyCode") String currencyCode,
                                   @Param(value = "currencyId") int currencyId);





    BigDecimal getCatBalance();
    BigDecimal getOldCatBalance();

    BigDecimal getCagBalance();
    BigDecimal getOldCagBalance();

}
