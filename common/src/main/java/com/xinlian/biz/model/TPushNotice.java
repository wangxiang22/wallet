package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 推送通知表
 */
@TableName("t_push_notice")
public class TPushNotice implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 推送标题
     */
    private String title;
    /**
     * 推送类型 - 1：图片推送，2：文字推送，3：全屏推送
     */
    private Integer type;
    /**
     * 推送图片
     */
    @TableField("push_image")
    private String pushImage;
    /**
     * 推送文字
     */
    @TableField("push_text")
    private String pushText;
    /**
     * 全屏推送链接
     */
    @TableField("push_url")
    private String pushUrl;
    /**
     * 是否可以关闭 - 0：不可关闭，1：可以关闭
     */
    @TableField("close_status")
    private Integer closeStatus;
    /**
     * 线上状态 - 0：下线，1：上线
     */
    @TableField("online_status")
    private Integer onlineStatus;
    /**
     * 推送状态 - 1：未开始，2：生效中，3：已失效
     */
    private Integer status;
    /**
     * 开始时间
     */
    @TableField("start_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date startTime;
    /**
     * 结束时间
     */
    @TableField("end_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date endTime;
    /**
     * 针对用户推送
     */
    private String uids;
    /**
     * 针对节点推送
     */
    @TableField("node_ids")
    private String nodeIds;
    /**
     * 定时推送id
     */
    @TableField("schedule_id")
    private String scheduleId;
    /**
     * 唯一标识码
     */
    @TableField("unique_code")
    private String uniqueCode;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getPushImage() {
        return pushImage;
    }

    public void setPushImage(String pushImage) {
        this.pushImage = pushImage;
    }

    public String getPushText() {
        return pushText;
    }

    public void setPushText(String pushText) {
        this.pushText = pushText;
    }

    public String getPushUrl() {
        return pushUrl;
    }

    public void setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
    }

    public Integer getCloseStatus() {
        return closeStatus;
    }

    public void setCloseStatus(Integer closeStatus) {
        this.closeStatus = closeStatus;
    }

    public Integer getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(Integer onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getUids() {
        return uids;
    }

    public void setUids(String uids) {
        this.uids = uids;
    }

    public String getNodeIds() {
        return nodeIds;
    }

    public void setNodeIds(String nodeIds) {
        this.nodeIds = nodeIds;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    @Override
    public String toString() {
        return "TPushNotice{" +
        ", id=" + id +
        ", title=" + title +
        ", type=" + type +
        ", pushImage=" + pushImage +
        ", pushText=" + pushText +
        ", pushUrl=" + pushUrl +
        ", closeStatus=" + closeStatus +
        ", onlineStatus=" + onlineStatus +
        ", status=" + status +
        ", startTime=" + startTime +
        ", endTime=" + endTime +
        ", uids=" + uids +
        ", nodeIds=" + nodeIds +
        ", scheduleId=" + scheduleId +
        ", uniqueCode=" + uniqueCode +
        "}";
    }
}
