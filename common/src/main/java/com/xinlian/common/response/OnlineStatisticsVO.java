package com.xinlian.common.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangJun
 * @version V1.0  2020/5/21
 **/
@Data
public class OnlineStatisticsVO implements Serializable {

    private static final long serialVersionUID = 324068146114398853L;

    private Integer liveOnline; // 实时在线人数
    private Integer more3hourOnline; // 在线人数(在线三小时以上)
//    private List<OnlineDetail> onlineDetails;


//    @Data
//    public static class OnlineDetail{
//        private String uid;
//        private String username;
//        private Integer todayDuration; // 今日在线时长
//        private Integer totalDuration; // 总在线时长
//    }

}
