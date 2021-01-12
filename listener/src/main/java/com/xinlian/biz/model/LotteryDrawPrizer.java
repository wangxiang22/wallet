package com.xinlian.biz.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 中奖者表
 * </p>
 *
 * @author lx
 * @since 2020-06-05
 */
@Data
public class LotteryDrawPrizer implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private Long uid;
    /**
     * 奖项
     */
    private String prize;
    /**
     * 奖金
     */
    private String value;
    /**
     * 中奖时间
     */
    private Date createTime;

    /**
     * 中奖人
     */
    private String username;


}
