package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 挖矿申请通过表
 * </p>
 *
 * @author 无名氏
 * @since 2020-04-26
 */
@TableName("mining_apply_ok")
@Data
public class MiningApplyOk implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 用户uid
     */
    private Integer uid;
    /**
     * 节点id
     */
    @TableField("node_id")
    private Integer nodeId;
    /**
     * 用户名
     */
    @TableField("user_name")
    private String userName;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 节点名
     */
    @TableField("node_name")
    private String nodeName;
    /**
     * 真实姓名
     */
    @TableField("auth_name")
    private String authName;
    /**
     * 身份证号
     */
    @TableField("auth_sn")
    private String authSn;

    /**
     * 1.审核中 2.未通过 3.通过
     */
    private Integer state;
    /**
     * 申请时间
     */
    @TableField("pass_time")
    private Date passTime;
    /**
     * 二维码随机数
     */
    @TableField("qr_code")
    private String qrCode;
    /**
     * token
     */
    private String token;
}
