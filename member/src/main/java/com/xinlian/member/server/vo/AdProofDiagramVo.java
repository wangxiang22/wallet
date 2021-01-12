package com.xinlian.member.server.vo;

import lombok.Data;

/**
 * @author Song
 * @date 2020-05-18 11:04
 * @description 人机广告验证Vo
 */
@Data
public class AdProofDiagramVo {

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
    private String adPercent;
}
