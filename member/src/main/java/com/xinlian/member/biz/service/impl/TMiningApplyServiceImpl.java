package com.xinlian.member.biz.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.xinlian.biz.model.TMiningApply;
import com.xinlian.biz.dao.TMiningApplyMapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.xinlian.common.request.FindAllUserReq;
import com.xinlian.common.result.BizException;
import com.xinlian.member.biz.jwt.util.JwtUtil;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.service.TMiningApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import java.util.Date;

import static com.xinlian.common.contants.MiningApplyConstant.*;
import static com.xinlian.common.redis.RedisConstant.*;

/**
 * <p>
 * 挖矿申请表 服务实现类
 * </p>
 *
 * @author 无名氏
 * @since 2020-04-25
 */
@Service
public class TMiningApplyServiceImpl extends ServiceImpl<TMiningApplyMapper, TMiningApply> implements TMiningApplyService {
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void mingingApply(TMiningApply tMiningApply) {
        try {
            long activeTime = (Long)(redisClient.get(MINING.concat(ACTIVE_TIME)));
            long nextActiveTime = (Long)(redisClient.get(MINING.concat(NEXT_ACTIVE_TIME)));
            if (!(System.currentTimeMillis() > activeTime
                    && System.currentTimeMillis() < nextActiveTime)) {
                throw new BizException("当前时间不在申请时间内,下次开放时间为:" + activeTime);
            }
            if ((Integer)(redisClient.get(MINING.concat(ACTIVE_COUNT))) <= 0) {
                throw new BizException(NO_ACTIVE_NUM);
            }
            int count = selectCount(new EntityWrapper<TMiningApply>().eq("auth_sn", tMiningApply.getAuthSn()));
            tMiningApply.setState(1);//设置审核中
            if (count > 0) {
                throw new BizException(ALREADY_APPLY);
            }
            tMiningApply.setApplyTime(new Date());
            insert(tMiningApply);
        } catch (Exception e) {
            throw new BizException(INFO_NOT_FULL);
        }
    }

    @Override
    public TMiningApply findUserApplyState() {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
        Long userId = jwtUtil.getUserId(request);
        TMiningApply tMiningApply = selectOne(new EntityWrapper<TMiningApply>().eq("uid", userId));
        return tMiningApply;
    }
}
