package com.xinlian.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PushNoticeRes {
    private String uniqueCode;//唯一标识码
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date endTime;//结束时间
    private List<Long> uidList;//针对用户推送
    private List<Long> nodeIdList;//针对节点推送
}
