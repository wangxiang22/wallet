package com.xinlian.biz.model;

import lombok.Data;

import java.util.Date;
import java.io.Serializable;

/**
 * <p>
 * 广告人机校验表
 * </p>
 *
 * @author WX
 * @since 2020-05-16
 */
@Data
public class AdProofDiagramModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 广告图title
     */
    private String adTitle;
    /**
     * 大图
     */
    private String imageUrl;
    /**
     * 子图
     */
    private String submapUrl;
    /**
     * 背景图
     */
    private String backgroundUrl;
    /**
     * 广告转向链接
     */
    private String jumpToUrl;
    /**
     * 百分比 * 100
     */
    private String percent;
    /**
     * 状态 1可用 2 禁用
     */
    private Integer status;
    private Date cratetime;



}
