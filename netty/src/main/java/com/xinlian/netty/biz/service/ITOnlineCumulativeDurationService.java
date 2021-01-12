package com.xinlian.netty.biz.service;

import com.baomidou.mybatisplus.service.IService;
import com.xinlian.biz.model.TOnlineCumulativeDuration;
import com.xinlian.common.response.OnlineRankVO;
import com.xinlian.common.response.UidRelatedUsernameVO;
import com.xinlian.common.response.UserTotalDurationVo;

import java.util.List;
import java.util.Set;

public interface ITOnlineCumulativeDurationService extends IService<TOnlineCumulativeDuration> {
    List<UserTotalDurationVo> userTotalDurations();


    List<UidRelatedUsernameVO> getUidRelatedUsername();

    List<OnlineRankVO> sortTotalDurationRank();

    List<UidRelatedUsernameVO> findUsernameByUidSet(Set<String> withoutNameUidSet);
}
