package com.xinlian.netty.biz.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.xinlian.biz.dao.TOnlineCumulativeDurationMapper;
import com.xinlian.biz.model.TOnlineCumulativeDuration;
import com.xinlian.common.response.OnlineRankVO;
import com.xinlian.common.response.UidRelatedUsernameVO;
import com.xinlian.common.response.UserTotalDurationVo;
import com.xinlian.netty.biz.service.ITOnlineCumulativeDurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @author zhangJun
 * @version V1.0  2020/4/26
 **/
@Service
public class TOnlineCumulativeDurationServiceImpl
        extends ServiceImpl<TOnlineCumulativeDurationMapper, TOnlineCumulativeDuration>
        implements ITOnlineCumulativeDurationService {

    @Autowired
    private TOnlineCumulativeDurationMapper onlineCumulativeDurationMapper;


    @Override
    public List<UidRelatedUsernameVO> findUsernameByUidSet(Set<String> withoutNameUidSet) {
        return onlineCumulativeDurationMapper.findUsernameByUidSet(withoutNameUidSet);
    }

    @Override
    public List<OnlineRankVO> sortTotalDurationRank() {
        return onlineCumulativeDurationMapper.sortTotalDurationRankList();
    }

    @Override
    public List<UidRelatedUsernameVO> getUidRelatedUsername() {
        return onlineCumulativeDurationMapper.getUidRelatedUsername();
    }

    @Override
    public List<UserTotalDurationVo> userTotalDurations() {
        return onlineCumulativeDurationMapper.userTotalDurations();
    }
}
