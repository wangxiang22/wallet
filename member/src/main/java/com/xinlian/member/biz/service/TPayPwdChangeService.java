package com.xinlian.member.biz.service;

import com.xinlian.biz.model.TPayPwdChange;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 * 支付密码变更表（用户手机号已不可用） 服务类
 * </p>
 *
 * @author lx
 * @since 2020-06-04
 */
public interface TPayPwdChangeService extends IService<TPayPwdChange> {

    TPayPwdChange queryState(Long userId);
}
