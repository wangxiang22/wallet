package com.xinlian.netty.biz.service;

import com.xinlian.common.response.OnlineRankStatisticsVO;
import com.xinlian.common.response.OnlineStatisticsVO;
import com.xinlian.common.response.ResponseResult;

public interface SysUserOnlineStatisticsService {
    ResponseResult<OnlineStatisticsVO> onlineStatistics();

    ResponseResult<OnlineRankStatisticsVO> onlineRank();
}
