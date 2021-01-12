package com.xinlian.biz.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xinlian.biz.model.TOnlineCumulativeDuration;
import com.xinlian.common.response.OnlineRankStatisticsVO;
import com.xinlian.common.response.OnlineRankVO;
import com.xinlian.common.response.UidRelatedUsernameVO;
import com.xinlian.common.response.UserTotalDurationVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface TOnlineCumulativeDurationMapper extends BaseMapper<TOnlineCumulativeDuration> {


    List<UserTotalDurationVo> userTotalDurations();

    List<UidRelatedUsernameVO> getUidRelatedUsername();

    List<OnlineRankVO> sortTotalDurationRankList();

    List<UidRelatedUsernameVO> findUsernameByUidSet(@Param("withoutNameUidSet") Set<String> withoutNameUidSet);
}
