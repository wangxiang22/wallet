package com.xinlian.admin.biz.service;

import com.baomidou.mybatisplus.service.IService;
import com.xinlian.biz.model.TNewsArticle;
import com.xinlian.common.request.NewsReq;
import com.xinlian.common.request.PageReq;
import com.xinlian.common.response.ResponseResult;

/**
 * <p>
 * 新闻文章表 服务类
 * </p>
 *
 * @author wx
 * @since 2019-12-23
 */
public interface TNewsArticleService extends IService<TNewsArticle> {
    ResponseResult queryAll(PageReq pageReq);
    ResponseResult deleteById(Long id);
    ResponseResult insertOne(TNewsArticle tNewsArticle);
    ResponseResult updateNews(TNewsArticle tNewsArticle);
    ResponseResult queryOne(Long id);
    ResponseResult searchNews(NewsReq newsReq);



}
