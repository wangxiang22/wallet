package com.xinlian.biz.model;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author lx
 * @since 2020-06-05
 */
@Data
public class LotteryDraw implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    /**
     * 奖项
     */
    private String prize;
    /**
     * 奖值
     */
    private String value;

    /**
     * 奖项code
     */
    private String code;
    /**
     * 活动内容
     */
    private String des;
    /**
     * 库存
     */
    private Integer stock;
    /**
     * num
     */
    private Integer num;

}
