package com.xinlian.member.biz.service;

import com.baomidou.mybatisplus.service.IService;
import com.xinlian.biz.model.TRecommend;
import com.xinlian.common.response.ResponseResult;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wx
 * @since 2020-03-25
 */
public interface TRecommendService extends IService<TRecommend> {


    ResponseResult<List<TRecommend>> queryRecommend(Long nodeId);
}
