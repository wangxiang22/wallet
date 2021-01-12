package com.xinlian.member.biz.service;

import com.xinlian.biz.model.TLike;
import com.baomidou.mybatisplus.service.IService;
import com.xinlian.common.request.LikeReq;
import com.xinlian.common.response.ResponseResult;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wx
 * @since 2020-03-27
 */
public interface TLikeService extends IService<TLike> {

    ResponseResult queryLike(LikeReq likeReq);
}
