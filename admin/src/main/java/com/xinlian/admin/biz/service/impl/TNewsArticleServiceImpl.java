package com.xinlian.admin.biz.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.xinlian.admin.biz.redis.RedisClient;
import com.xinlian.admin.biz.redis.RedisConstant;
import com.xinlian.admin.biz.service.TNewsArticleService;
import com.xinlian.biz.dao.AdminOptionsMapper;
import com.xinlian.biz.dao.TNewsArticleMapper;
import com.xinlian.biz.model.AdminOptions;
import com.xinlian.biz.model.TNewsArticle;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.request.NewsReq;
import com.xinlian.common.request.PageReq;
import com.xinlian.common.response.NewsRes;
import com.xinlian.common.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 新闻文章表 服务实现类
 * </p>
 *
 * @author wx
 * @since 2019-12-23
 */
@Service
public class TNewsArticleServiceImpl extends ServiceImpl<TNewsArticleMapper, TNewsArticle> implements TNewsArticleService {

    @Autowired
    private TNewsArticleMapper tNewsArticleMapper;
    @Autowired
    private AdminOptionsMapper adminOptionsMapper;
  /*  @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedisClient redisClient;*/

    public String pre() {
        //拿到数据库中存的域名
        EntityWrapper<AdminOptions> wrapper = new EntityWrapper<>();
        wrapper.eq("option_name", "siteUrl");
        List<AdminOptions> list = adminOptionsMapper.selectList(wrapper);
        if (list.isEmpty()) {
            return "";
        }
        AdminOptions adminOptions = list.get(0);
        String prefUrl = adminOptions.getOptionValue();
        return prefUrl;
    }


    //根据id获取文章的详细信息
    @Override
    public ResponseResult queryOne(Long id) {
        ResponseResult<Object> result = new ResponseResult<>();
        TNewsArticle tNewsArticle = tNewsArticleMapper.queryOne(id);
        String pre = pre();
        if (null == tNewsArticle) {
            result.setCode(GlobalConstant.ResponseCode.PARAM_ERROR);
            result.setMsg("请求参数不合法");
            return result;
        }
        String url = tNewsArticle.getUrl();
        String newUrl = pre + url;
        tNewsArticle.setUrl(newUrl);

        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        result.setResult(tNewsArticle);
        result.setMsg("请求成功");
        return result;
    }


    //查看所有的新闻，包括文章，新手帮助，banner，等等
    @Override
    public ResponseResult queryAll(PageReq pageReq) {
        ResponseResult<Object> result = new ResponseResult<>();
        try {
            List<NewsRes> tNewsArticles = tNewsArticleMapper.queryAll();
            String pre = pre();
            for (NewsRes tNewsArticle : tNewsArticles) {
                String url = tNewsArticle.getUrl();
                String newUrl = pre + url;
                tNewsArticle.setUrl(newUrl);
            }
            if (pageReq.getCurPage() == null) {
                pageReq.setCurPage(1L);
            }
            int fromIndex = 10 * (pageReq.getCurPage().intValue() - 1);
            int toIndex = (Math.min((10 * pageReq.getCurPage().intValue()), tNewsArticles.size()));
            if (fromIndex > toIndex) {
                fromIndex = toIndex;
            }
           int count = tNewsArticles.size();
            List<NewsRes> newsRes = tNewsArticles.subList(fromIndex, toIndex);
            result.setCode(GlobalConstant.ResponseCode.SUCCESS);
            result.setMsg("请求成功");
            result.setStatus(""+count);
            result.setResult(newsRes);
            return result;
        } catch (Exception e) {
            result.setMsg("请求参数有误");
            return result;
        }
    }

    //根据id删除新闻
    @Override
    public ResponseResult deleteById(Long id) {
        ResponseResult<Object> result = new ResponseResult<>();
        Integer integer = tNewsArticleMapper.deleteById(id);
        if (integer == 0) {
            result.setCode(GlobalConstant.ResponseCode.FAIL);
            result.setMsg("删除失败");
            return result;
        }
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        result.setMsg("删除成功");
        result.setResult(integer);
     /*   if (null != redisTemplate.keys(RedisConstant.QUERY_LIST_FOR_ARTICLE_KEY) || null != redisClient.get(RedisConstant.QUERY_LIST_FOR_TEN_ARTICLE_KEY)) {
            //模糊删除
            redisTemplate.delete(redisTemplate.keys("*"+RedisConstant.QUERY_LIST_FOR_ARTICLE_KEY + "*"));
            redisClient.deleteByKey(RedisConstant.QUERY_LIST_FOR_TEN_ARTICLE_KEY);
        }*/
        return result;
    }

    //插入一条数据
    @Override
    @Transactional
    public ResponseResult insertOne(TNewsArticle tNewsArticle) {
        ResponseResult<Object> result = new ResponseResult<>();
        Integer num = tNewsArticleMapper.insertOne(tNewsArticle);
        Long id = tNewsArticle.getId();
        String url = "/new/#/?id=" + id;
        tNewsArticle.setUrl(url);
        tNewsArticle.setLikes(0L);
        tNewsArticleMapper.updateById(tNewsArticle);
        if (num <= 0) {
            result.setCode(GlobalConstant.ResponseCode.FAIL);
            result.setMsg("新增失败");
            return result;
        }
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        result.setMsg("新增成功");
        result.setResult(num);
        return result;
    }


    //修改新闻相关
    @Override
    @Transactional
    public ResponseResult updateNews(TNewsArticle tNewsArticle) {
        ResponseResult<Object> result = new ResponseResult<>();
        Integer integer = tNewsArticleMapper.updateById(tNewsArticle);
        if (integer == 0) {
            result.setMsg("更新失败，请稍后重试");
            return result;
        }
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        result.setMsg("更新成功");
        result.setResult(integer);
      /*  if (null != redisTemplate.keys(RedisConstant.QUERY_LIST_FOR_ARTICLE_KEY) || null != redisClient.get(RedisConstant.QUERY_LIST_FOR_TEN_ARTICLE_KEY)) {
            //模糊删除
           // System.out.println(RedisConstant.QUERY_LIST_FOR_ARTICLE_KEY);
            redisTemplate.delete(redisTemplate.keys("*"+RedisConstant.QUERY_LIST_FOR_ARTICLE_KEY + "*"));
            redisClient.deleteByKey(RedisConstant.QUERY_LIST_FOR_TEN_ARTICLE_KEY);
        }*/
        return result;
    }


    //根据关键字搜索
    @Override
    public ResponseResult searchNews(NewsReq newsReq) {
        ResponseResult<Object> result = new ResponseResult<>();
        try {
            List<NewsRes> newsResList = tNewsArticleMapper.searchNews(newsReq.getTid(), newsReq.getTitle(), newsReq.getStartTime(), newsReq.getEndTime());
            if (null == newsResList || newsResList.size() == 0) {
                result.setMsg("未找到该内容");
                result.setCode(GlobalConstant.ResponseCode.SUCCESS);
                return result;
            }
            String pre = pre();
            for (NewsRes tNewsArticle : newsResList) {
                String url = tNewsArticle.getUrl();
                String newUrl = pre + url;
                tNewsArticle.setUrl(newUrl);
            }
            if (newsReq.getCurPage() == null) {
                newsReq.setCurPage(1L);
            }

            int fromIndex = 10 * (newsReq.getCurPage().intValue() - 1);
            int toIndex = (Math.min((10 * newsReq.getCurPage().intValue()), newsResList.size()));
            if (fromIndex > toIndex) {
                fromIndex = toIndex;
            }
            List<NewsRes> newsRes = newsResList.subList(fromIndex, toIndex);
            result.setCode(GlobalConstant.ResponseCode.SUCCESS);
            result.setMsg("请求成功");
            result.setStatus(""+newsResList.size());
            result.setResult(newsRes);
            return result;
        }catch (Exception e){
            result.setMsg("请检查参数是否异常");
            return result;
        }
    }

}