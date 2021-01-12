package com.xinlian.member.biz.service.impl;

import com.xinlian.biz.model.TPayPwdChange;
import com.xinlian.biz.dao.TPayPwdChangeMapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.xinlian.member.biz.service.TPayPwdChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 支付密码变更表（用户手机号已不可用） 服务实现类
 * </p>
 *
 * @author lx
 * @since 2020-06-04
 */
@Service
public class TPayPwdChangeServiceImpl extends ServiceImpl<TPayPwdChangeMapper, TPayPwdChange> implements TPayPwdChangeService {
    @Autowired
    private TPayPwdChangeMapper tPayPwdChangeMapper;

    @Override
    public TPayPwdChange queryState(Long userId) {
        TPayPwdChange tPayPwdChange = tPayPwdChangeMapper.queryState(userId);
        return tPayPwdChange;
    }
}
