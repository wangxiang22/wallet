package com.xinlian.admin.biz.service;

import com.baomidou.mybatisplus.service.IService;
import com.xinlian.biz.model.TRecommend;
import com.xinlian.common.response.ResponseResult;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wx
 * @since 2020-03-25
 */
public interface TRecommendService extends IService<TRecommend> {


    ResponseResult queryRecommend();

    ResponseResult deleteRecommend(int id);

    ResponseResult updateRecommend(TRecommend tRecommend);

    ResponseResult insertRecommend(TRecommend tRecommend);


}
