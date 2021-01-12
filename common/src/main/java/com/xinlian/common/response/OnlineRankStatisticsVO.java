package com.xinlian.common.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangJun
 * @version V1.0  2020/5/21
 **/
@Data
public class OnlineRankStatisticsVO implements Serializable {
    private static final long serialVersionUID = 6518131890030338036L;

    private List<OnlineRankVO> todayRank;
    private List<OnlineRankVO> totalRank;

}
