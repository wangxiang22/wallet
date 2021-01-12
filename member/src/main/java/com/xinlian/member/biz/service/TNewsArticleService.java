package com.xinlian.member.biz.service;

import com.baomidou.mybatisplus.service.IService;
import com.xinlian.biz.model.TLike;
import com.xinlian.biz.model.TNewsArticle;
import com.xinlian.common.request.NewsReq;
import com.xinlian.common.request.PageReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.optionsconfig.ActivityConfig;

/**
 * <p>
 * 新闻文章表 服务类
 * </p>
 *
 * @author wx
 * @since 2019-12-23
 */
public interface TNewsArticleService extends IService<TNewsArticle> {
    ResponseResult queryActivity(ActivityConfig activityConfig);
    ResponseResult queryOne(TLike tLike);
    ResponseResult queryInform(PageReq pageReq);
    ResponseResult searchNewsAll(NewsReq newsReq);
    ResponseResult updateHits(int id);
}
