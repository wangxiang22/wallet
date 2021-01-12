package com.xinlian.common.response;

import lombok.Data;

@Data
public class NewsRes {

    private Long id;
    /**
     * 文章标题
     */
    private String title;
    /**
     * 描述
     */
    private String description;
    /**
     * 关键字
     */
    private String keywords;
    /**
     * 文章内容
     */
    private String content;
    /**
     * 文章地址
     */
    private String url;
    /**
     * 图片地址
     */
    private String thumb;
    /**
     * 文章标签：默认是1，1是普通文章，2是新的文章，3是热度文章
     */

    private Integer label;
    /**
     * 点赞数
     */

    private Long likes;
    /**
     * 是否置顶
     */

    private Integer topStatus;
    /**
     * 作者id
     */
    private Integer uid;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 创建时间
     */

    private Long inputTime;
    /**
     * 更新人
     */
    private String updator;
    /**
     * 更新时间
     */

    private Long updateTime;
    /**
     * 浏览数
     */
    private Long hits;

    /**
     * 新闻类型
     * */
   private String name;
   /**
   * 新闻状态，是否显示，0 不显示，1显示
   * */
   private Integer status;
    /**
     * 中英文相关
     * */
   private String typeLanguage;

   private Integer tid;
   private String uidS;
   private String outUrl;

}
