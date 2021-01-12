package com.xinlian.admin.biz.service;

import com.xinlian.biz.model.TNewsArticle;
import com.xinlian.biz.model.TNewsType;
import com.baomidou.mybatisplus.service.IService;
import com.xinlian.common.response.ResponseResult;

/**
 * <p>
 * banner图表 服务类
 * </p>
 *
 * @author wx
 * @since 2020-03-03
 */
public interface TNewsTypeService extends IService<TNewsType> {

    ResponseResult queryAll();
    ResponseResult deleteById(Long id);
    ResponseResult insertOne(TNewsType tNewsType);
    ResponseResult updateNewsType(TNewsType tNewsType);

}
