package com.xinlian.member.biz.service;

import com.baomidou.mybatisplus.service.IService;
import com.xinlian.biz.model.TNewsType;
import com.xinlian.common.response.ResponseResult;

/**
 * <p>
 * banner图表 服务类
 * </p>
 *
 * @author wx
 * @since 2020-03-04
 */
public interface TNewsTypeService extends IService<TNewsType> {

    ResponseResult queryAll();


}
