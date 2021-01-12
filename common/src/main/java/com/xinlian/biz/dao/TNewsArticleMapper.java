package com.xinlian.biz.dao;

import com.xinlian.common.request.NewsReq;
import com.xinlian.common.response.NewsRes;
import com.xinlian.biz.model.TNewsArticle;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface TNewsArticleMapper extends BaseMapper<TNewsArticle> {

    List<TNewsArticle>  queryListForNew();
   TNewsArticle queryOne(Long id);
  List<NewsRes> queryAll();
    Integer insertOne(TNewsArticle tNewsArticle);
    List<NewsRes> searchNews(Integer tid,String title,Long startTime,Long endTime);
    List<TNewsArticle>  searchNewsAllCn(NewsReq newsReq);
    List<TNewsArticle>  searchNewsAllEn(NewsReq newsReq);
    Integer updateHits(int id);
    Integer updateLikes(long likes,long id);

}
