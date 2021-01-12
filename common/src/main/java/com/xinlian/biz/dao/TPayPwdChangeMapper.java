package com.xinlian.biz.dao;

import com.xinlian.biz.model.TPayPwdChange;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 支付密码变更表（用户手机号已不可用） Mapper 接口
 * </p>
 *
 * @author lx
 * @since 2020-06-04
 */
@Repository
public interface TPayPwdChangeMapper extends BaseMapper<TPayPwdChange> {

    TPayPwdChange queryState(Long userId);
}
