package com.xinlian.biz.model;

import com.baomidou.mybatisplus.enums.IdType;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 会员表
 * </p>
 *
 * @author WX
 * @since 2020-05-16
 */
@TableName("oldData_dr_member")
@Data
public class OlddataDrMember implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "uid", type = IdType.AUTO)
    private Integer uid;
    /**
     * 邮箱地址
     */
    private String email;
    /**
     * 用户名
     */
    private String username;
    /**
     * 加密密码
     */
    private String password;
    /**
     * 随机加密码
     */
    private String salt;
    /**
     * 姓名
     */
    private String name;
    /**
     * 手机号码
     */
    private String phone;
    /**
     * 头像地址
     */
    private String avatar;
    /**
     * RMB
     */
    private BigDecimal money;
    /**
     * 冻结RMB
     */
    private BigDecimal freeze;
    /**
     * 消费RMB总额
     */
    private BigDecimal spend;
    /**
     * 虚拟币
     */
    private Integer score;
    /**
     * 经验值
     */
    private Integer experience;
    /**
     * 管理组id
     */
    private Integer adminid;
    /**
     * 用户组id
     */
    private Integer groupid;
    /**
     * 会员级别
     */
    private Integer levelid;
    /**
     * 到期时间
     */
    private Integer overdue;
    /**
     * 注册ip
     */
    private String regip;
    /**
     * 注册时间
     */
    private Integer regtime;
    /**
     * 随机验证码
     */
    private Integer randcode;
    /**
     * 手机认证标识
     */
    private Integer ismobile;
    /**
     * 老系统用户ID
     */
    private Integer oldId;
    /**
     * 会员类型
     */
    private Integer systemType;
    /**
     * 上级id
     */
    private Integer parentId;
    /**
     * 会员关系路径
     */
    private String path;
    /**
     * 0 :未激活 1:已激活
     */
    private Integer active;
    /**
     * 激活时间
     */
    private Integer activeTime;
    /**
     * 邀请人数
     */
    private Integer inviteNum;
    private String token;
    /**
     * 推送状态
     */
    private Integer jpush;
    /**
     * 关联火箭交易关系

     */
    private Integer isrocket;
    private Integer lStatus;
    /**
     * sanxia

     */
    private Integer san;



}
