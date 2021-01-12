package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author wjf
 * @since 2020-01-02
 */
@TableName("t_version")
public class TVersion implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 版本号
     */
    @TableField("versionId")
    private String versionId;
    /**
     * 地址
     */
    private String addr;
    /**
     * 创建时间
     */
    @TableField("createdTime")
    private Long createdTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String toString() {
        return "TVersion{" +
        ", id=" + id +
        ", versionId=" + versionId +
        ", addr=" + addr +
        ", createdTime=" + createdTime +
        "}";
    }
}
