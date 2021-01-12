package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 上传链权人信息记录表
 * </p>
 *
 * @author 代码生成
 * @since 2020-01-14
 */
@Data
public class TUploadChainOwnerRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 用户id
     */
    private Long uid;
    /**
     * 节点id
     */
    private Long nodeId;
    /**
     * 状态 - 1：未处理，2：处理中，3：处理完成，4：处理失败
     */
    private Integer status;
    /**
     * 用户邮箱
     */
    private String email;
    /**
     * 用户实名姓名
     */
    private String authName;
    /**
     * 用户实名证件号
     */
    private String authSn;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;

    @Override
    public String toString() {
        return "TUploadChainOwnerRecord{" +
        ", id=" + id +
        ", uid=" + uid +
        ", nodeId=" + nodeId +
        ", status=" + status +
        ", email=" + email +
        ", authName=" + authName +
        ", authSn=" + authSn +
        ", mobile=" + mobile +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        "}";
    }

    /**作为比对**/
    private Long userNodeId;
    private String userAuthName;
    private String userAuthSn;
    private String userMobile;
    private String userEmail;
    private BigDecimal defaultCatNum;
}
