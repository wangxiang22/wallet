package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 用户接收消息表
 * </p>
 *
 * @author lt
 * @since 2020-06-13
 */
@Data
@TableName("admin_receive_message")
public class AdminReceiveMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 消息接收方 - 三种情况，-1：全部用户，一个uid：一个用户，多个uid用英文逗号拼接：指定用户组
     */
    @TableField("uids")
    private String uids;
    /**
     * 消息内容id
     */
    @TableField("message_id")
    private Long messageId;
    /**
     * 消息接收方类型 - 1：全部用户，2：一个用户，3：一组用户
     */
    @TableField("role_type")
    private Integer roleType;
    /**
     * 创建时间
     */
    @TableField("create_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;


}
