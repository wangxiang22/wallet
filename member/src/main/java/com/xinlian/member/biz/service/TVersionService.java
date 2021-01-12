package com.xinlian.member.biz.service;

import com.baomidou.mybatisplus.service.IService;
import com.xinlian.biz.model.TVersion;
import com.xinlian.common.response.ResponseResult;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wjf
 * @since 2020-01-02
 */
public interface TVersionService extends IService<TVersion> {

    ResponseResult queryOne();
}
