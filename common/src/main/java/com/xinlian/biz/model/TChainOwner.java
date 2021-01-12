package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@TableName("t_chain_owner")
@Data
public class TChainOwner implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 用户id
     */
    @TableField("uid")
    private Long uid;
    /**
     * 节点id
     */
    @TableField("node_id")
    private Long nodeId;
    /**
     * 用户签约链权人状态 - 0：未签约，1：已签约
     */
    @TableField("sign_status")
    private Integer signStatus;
    /**
     * 用户邮箱
     */
    @TableField("email")
    private String email;
    /**
     * 用户实名姓名
     */
    @TableField("auth_name")
    private String authName;
    /**
     * 用户实名证件号
     */
    @TableField("auth_sn")
    private String authSn;
    /**
     * 链权人证书地址
     */
    @TableField("url")
    private String url;
    /**
     * 链权人锁仓cat数量
     */
    @TableField("cat_amount")
    private BigDecimal catAmount;
    /**
     * 创建时间
     */
    @TableField("create_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;
    /**
     * 更新时间
     */
    @TableField("update_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;


    /**查询条件**/
    @TableField(exist = false)
    private String userName;//用户名
    @TableField(exist = false)
    private String mobile;//手机号码
    /**接收参数**/
    @TableField(exist = false)
    private String serverNodeName;//节点名称


    @Override
    public String toString() {
        return "TChainOwner{" +
        ", id=" + id +
        ", uid=" + uid +
        ", nodeId=" + nodeId +
        ", signStatus=" + signStatus +
        ", email=" + email +
        ", authName=" + authName +
        ", authSn=" + authSn +
        ", url=" + url +
        ", catAmount=" + catAmount +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        "}";
    }
}
