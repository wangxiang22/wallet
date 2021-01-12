package com.xinlian.biz.model;

import lombok.Data;
import java.io.Serializable;

@Data
public class TrcUsdtAddressPool implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 链地址
     */
    private String addressBase58;

    /**
     * 消费状态 1未消费  2 已消费
     */
    private Integer status;

    private Integer oldStatus;


}
