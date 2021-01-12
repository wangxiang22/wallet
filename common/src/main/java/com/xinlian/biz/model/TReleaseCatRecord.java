package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 释放cat记录表
 * </p>
 *
 * @author 插件生成
 * @since 2020-01-13
 */
@Data
public class TReleaseCatRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * uid
     */
    private Long uid;
    /**
     * 姓名
     */
    private String userName;
    /**
     * 客户登录名称
     */
    private String userLoginName;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 释放cat数量
     */
    private BigDecimal releaseCatNum;
    /**
     * 处理状态 1 未处理 2 处理中 3 处理完成
     */
    private Integer status;
    /**
     * 导入时间
     */
    private Date createtime;
    /**
     * 更新时间
     */
    private Date updatetime;




    @Override
    public String toString() {
        return "TReleaseCatRecord{" +
        ", id=" + id +
        ", uid=" + uid +
        ", userName=" + userName +
        ", userLoginName=" + userLoginName +
        ", mobile=" + mobile +
        ", releaseCatNum=" + releaseCatNum +
        ", status=" + status +
        ", createtime=" + createtime +
        ", updatetime=" + updatetime +
        "}";
    }
}
