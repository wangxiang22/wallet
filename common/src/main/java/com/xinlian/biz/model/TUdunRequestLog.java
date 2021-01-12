package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TUdunRequestLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 主键
     */
    private Long uid;

    private String udunOpeType;
    /**
     * 请求参数
     */
    @TableField("requestBody")
    private String requestBody;
    /**
     * 返回参数
     */
    @TableField("responseBody")
    private String responseBody;

    /**
     * 请求时间
     */
    private Date createTime;

    @Override
    public String toString() {
        return "TUdunRequestLog{" +
        ", id=" + id +
        ", uid=" + uid +
        ", requestBody=" + requestBody +
        ", responseBody=" + responseBody +
        ", udunOpeType=" + udunOpeType +
        ", createTime=" + createTime +
        "}";
    }
}
