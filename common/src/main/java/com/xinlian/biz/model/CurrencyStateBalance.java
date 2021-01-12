package com.xinlian.biz.model;


import com.baomidou.mybatisplus.annotations.TableField;

import java.math.BigDecimal;

public class CurrencyStateBalance {

    @TableField("ex_to_wallet")
    private Integer exToWallet;
    @TableField("wallet_to_exchange")
    private Integer walletToEx;
    @TableField("balance_num")
    private BigDecimal balanceNum;

}
