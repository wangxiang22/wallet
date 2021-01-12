package com.xinlian.member.biz.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.base.Splitter;
import com.xinlian.biz.dao.AdminOptionsMapper;
import com.xinlian.biz.dao.TLikeMapper;
import com.xinlian.biz.dao.TNewsArticleMapper;
import com.xinlian.biz.model.AdminOptions;
import com.xinlian.biz.model.TLike;
import com.xinlian.biz.model.TNewsArticle;
import com.xinlian.biz.utils.AdminOptionsUtil;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.dto.NewsArticleDto;
import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
import com.xinlian.common.request.NewsReq;
import com.xinlian.common.request.PageReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.jwt.util.JwtUtil;
import com.xinlian.member.biz.optionsconfig.ActivityConfig;
import com.xinlian.member.biz.service.TNewsArticleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
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
@Slf4j
public class TNewsArticleServiceImpl extends ServiceImpl<TNewsArticleMapper, TNewsArticle> implements TNewsArticleService {
    private final Logger logger = LoggerFactory.getLogger(TNewsArticleServiceImpl.class);

    @Autowired
    private TNewsArticleMapper tNewsArticleMapper;
    @Autowired
    private AdminOptionsMapper adminOptionsMapper;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private JwtUtil util;
    @Autowired
    private AdminOptionsUtil adminOptionsUtil;


    private String pre() {
        //拿到数据库中存的域名
        EntityWrapper<AdminOptions> wrapper = new EntityWrapper<>();
        wrapper.eq("option_name", "siteUrl");
        List<AdminOptions> list = adminOptionsMapper.selectList(wrapper);
        if (list.size() == 0 ||  null==list) {
            return "";
        }
        AdminOptions adminOptions = list.get(0);
        String prefUrl = adminOptions.getOptionValue();
        return prefUrl;
    }

    private ResponseResult resultSuccess() {
        ResponseResult<Object> result = new ResponseResult<>();
        result.setMsg("请求成功");
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        return result;
    }

    /**
     * 获取底部导航栏的数据显示
     * @return
     */
    @Override
    public ResponseResult queryActivity(ActivityConfig activityConfig) {
        ResponseResult<Object> result = new ResponseResult<>();
        try {
            activityConfig = adminOptionsUtil.fieldEntityObject(AdminOptionsBelongsSystemCodeEnum.APP_ACT.getBelongsSystemCode(), ActivityConfig.class);
        }catch (Exception e){
            log.error("获取底部导航栏数据出现异常：{}",e.toString(),e);
        }
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        result.setResult(activityConfig);
        result.setMsg("请求成功");
        return result;
    }

    //通知，节点和uid
    //如果uids 字段是空的就走sys
    @Override
    public ResponseResult queryInform(PageReq pageReq) {
        ResponseResult<TNewsArticle> result = new ResponseResult<>();
        if (pageReq.getType()==2){
            pageReq.setType(1);
        }
        List<TNewsArticle> list = pageReq.getType() == 1
                ? tNewsArticleMapper.selectList(new EntityWrapper<TNewsArticle>()
                .eq("status",1)
                .eq("tid", 9).
                and().eq("type_language", "CN").orderDesc(Collections.singletonList("input_time")))
                : tNewsArticleMapper.selectList(new EntityWrapper<TNewsArticle>()
                .eq("status",1)
                .eq("tid", 9).
                and().eq("type_language", "EN").orderDesc(Collections.singletonList("input_time")));
        try {
            if (list.size() == 0 || null == list) {
                return resultSuccess();
            }
            for (TNewsArticle tNewsArticle : list) {
                String uidS = tNewsArticle.getUidS();
                if ("".equals(uidS) ||  null == uidS) {
                    //如果uids是空的，也就是说通知要给配置节点的人看
                    return queryAllInform(pageReq);
                } else {
                    return queryAllInformWithUid(pageReq);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    //获取通知，区分节点
    private ResponseResult queryAllInform(PageReq pageReq) {
        ResponseResult result = new ResponseResult();
        String pre = pre();
        Long nodeId = util.getNodeId(request);
        if (pageReq.getType()==2){
            pageReq.setType(1);
        }
        List<TNewsArticle> list = pageReq.getType() == 1
                ? tNewsArticleMapper.selectList(new EntityWrapper<TNewsArticle>()
                .eq("status",1)
                .eq("tid", 9)
                .and().eq("type_language", "CN")
                .and().eq("uids","")
                .orderDesc(Collections.singletonList("input_time")))
                : tNewsArticleMapper.selectList(new EntityWrapper<TNewsArticle>()
                .eq("status",1)
                .eq("tid", 9)
                .and().eq("type_language", "EN")
                .and().eq("uids","")
                .orderDesc(Collections.singletonList("input_time")));
        try {
            if (list.size() == 0 || null == list) {
                return resultSuccess();
            }
            for (int i = list.size() - 1; i >= 0; i--) {
                TNewsArticle tNewsArticle = list.get(i);
                if (null==tNewsArticle.getOutUrl()||"".equals(tNewsArticle.getOutUrl())){
                    String systemType = tNewsArticle.getSystemType();
                    if (null != systemType && systemType.length() != 0) {
                        List<String> strings = Splitter.on(",").omitEmptyStrings().splitToList(systemType);
                        strings.stream().filter(s -> s.equals(nodeId.toString())).forEach(s -> list.remove(tNewsArticle));
                    }
                    String url = tNewsArticle.getUrl();
                    if (StringUtils.isEmpty(url)) {
                        tNewsArticle.setUrl(url);
                    }
                    String newUrl = pre + url;
                    tNewsArticle.setUrl(newUrl);
                }else {
                    String systemType = tNewsArticle.getSystemType();
                    if (null != systemType && systemType.length() != 0) {
                        List<String> strings = Splitter.on(",").omitEmptyStrings().splitToList(systemType);
                        strings.stream().filter(s -> s.equals(nodeId.toString())).forEach(s -> list.remove(tNewsArticle));
                    }
                    tNewsArticle.setUrl(tNewsArticle.getOutUrl());
                }

            }
            int fromIndex = 10 * (pageReq.getCurPage().intValue() - 1);
            int toIndex = (Math.min((10 * pageReq.getCurPage().intValue()), list.size()));
            if (fromIndex > toIndex) {
                fromIndex = toIndex;
            }
            List<TNewsArticle> list1 = list.subList(fromIndex, toIndex);
            result.setCode(GlobalConstant.ResponseCode.SUCCESS);
            result.setMsg("请求成功");
            result.setResult(list1);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg("网络异常，请稍后重试");
            return result;
        }
    }

    //通知，用户uid
    private ResponseResult queryAllInformWithUid(PageReq pageReq) {
        ResponseResult result = new ResponseResult();
        String pre = pre();
        Long userId = util.getUserId(request);
        if (pageReq.getType()==2){
            pageReq.setType(1);
        }
        ArrayList<TNewsArticle> list = new ArrayList<>();
        List<TNewsArticle> articleList = pageReq.getType() == 1
                ? tNewsArticleMapper.selectList(new EntityWrapper<TNewsArticle>()
                .eq("status",1)
                .eq("tid", 9)
                .and().eq("type_language", "CN")
                .and().eq("system_type","")
                .orderDesc(Collections.singletonList("input_time")))
                : tNewsArticleMapper.selectList(new EntityWrapper<TNewsArticle>()
                .eq("status",1)
                .eq("tid", 9)
                .and().eq("type_language", "EN")
                .and().eq("system_type","")
                .orderDesc(Collections.singletonList("input_time")));
        try {
            if (articleList.size() == 0 ||  null ==articleList) {
                return resultSuccess();
            }
            for (int i = articleList.size() - 1; i >= 0; i--) {
                TNewsArticle tNewsArticle = articleList.get(i);
                if (null==tNewsArticle.getOutUrl()||"".equals(tNewsArticle.getOutUrl())){
                    String uidS = tNewsArticle.getUidS();
                    if (tNewsArticle.getUidS().equals("全部")){
                        String url = tNewsArticle.getUrl();
                        if (StringUtils.isEmpty(url)) {
                            tNewsArticle.setUrl(url);
                        }
                        String newUrl = pre + url;
                        tNewsArticle.setUrl(newUrl);
                        int fromIndex = 10 * (pageReq.getCurPage().intValue() - 1);
                        int toIndex = (Math.min((10 * pageReq.getCurPage().intValue()), articleList.size()));
                        if (fromIndex > toIndex) {
                            fromIndex = toIndex;
                        }
                        List<TNewsArticle> list1 = articleList.subList(fromIndex, toIndex);
                        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
                        result.setMsg("请求成功");
                        result.setResult(list1);
                        return result;
                    }
                    if (null != uidS && uidS.length() != 0) {
                        List<String> strings = Splitter.on(",").omitEmptyStrings().splitToList(uidS);
                        strings.stream().filter(s -> s.equals(userId.toString())).forEach(s -> list.add(tNewsArticle));
                        String url = tNewsArticle.getUrl();
                        if (StringUtils.isEmpty(url)) {
                            tNewsArticle.setUrl(url);
                        }
                        String newUrl = pre + url;
                        tNewsArticle.setUrl(newUrl);
                    }
                }else {
                    if (tNewsArticle.getUidS().equals("全部")){
                        tNewsArticle.setUrl(tNewsArticle.getOutUrl());
                        int fromIndex = 10 * (pageReq.getCurPage().intValue() - 1);
                        int toIndex = (Math.min((10 * pageReq.getCurPage().intValue()), articleList.size()));
                        if (fromIndex > toIndex) {
                            fromIndex = toIndex;
                        }
                        List<TNewsArticle> list1 = articleList.subList(fromIndex, toIndex);
                        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
                        result.setMsg("请求成功");
                        result.setResult(list1);
                        return result;
                    }
                    String uidS = tNewsArticle.getUidS();
                    if (null != uidS && uidS.length() != 0) {
                        List<String> strings = Splitter.on(",").omitEmptyStrings().splitToList(uidS);
                        strings.stream().filter(s -> s.equals(userId.toString())).forEach(s -> list.add(tNewsArticle));
                        tNewsArticle.setUrl(tNewsArticle.getOutUrl());
                    }
                }
            }
            int fromIndex = 10 * (pageReq.getCurPage().intValue() - 1);
            int toIndex = (Math.min((10 * pageReq.getCurPage().intValue()), list.size()));
            if (fromIndex > toIndex) {
                fromIndex = toIndex;
            }
            List<TNewsArticle> list1 = list.subList(fromIndex, toIndex);
            result.setCode(GlobalConstant.ResponseCode.SUCCESS);
            result.setMsg("请求成功");
            result.setResult(list1);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg("网络异常，请稍后重试");
            return result;
        }
    }

    //查询所有
    @Override
    public ResponseResult searchNewsAll(NewsReq newsReq) {
        ResponseResult<Object> result = new ResponseResult<>();
        String pre = pre();
        Long nodeId = util.getNodeId(request);
        Long userId = util.getUserId(request);
        if (null==newsReq.getType() || newsReq.getType()==2 ){
            newsReq.setType(1);
        }
        /*List<TNewsArticle> tNewsList = redisClient.get(""+newsReq.getTid()+RedisConstant.QUERY_LIST_FOR_ARTICLE_KEY+""+newsReq.getCurPage());*/
        List<TNewsArticle> tNewsArticleList = newsReq.getType() == 1
                ? tNewsArticleMapper.searchNewsAllCn(newsReq)
                : tNewsArticleMapper.searchNewsAllEn(newsReq);

            try {
                if (tNewsArticleList.size() == 0 ||  null ==tNewsArticleList) {
                    return resultSuccess();
                }
                for (int i = tNewsArticleList.size() - 1; i >= 0; i--) {
                    TNewsArticle tNewsArticle = tNewsArticleList.get(i);
                    String systemType = tNewsArticle.getSystemType();
                    String uidS = tNewsArticle.getUidS();
                    String url = tNewsArticle.getUrl();
                    if (null==tNewsArticle.getOutUrl()||"".equals(tNewsArticle.getOutUrl())){
                        //如果节点写了，就是不让这个节点看到
                        if (null != systemType && systemType.length() != 0) {
                            List<String> strings = Splitter.on(",").omitEmptyStrings().splitToList(systemType);
                            strings.stream().filter(s -> s.equals(nodeId.toString())).forEach(s -> tNewsArticleList.remove(tNewsArticle));
                        }
                        if (null != uidS && uidS.length() != 0) {
                            List<String> strings = Splitter.on(",").omitEmptyStrings().splitToList(uidS);
                            strings.stream().filter(s -> s.equals(userId.toString())).forEach(s -> tNewsArticleList.remove(tNewsArticle));
                        }
                        if (StringUtils.isEmpty(url)) {
                            tNewsArticle.setUrl(url);
                        }
                        String newUrl = pre + url;
                        tNewsArticle.setUrl(newUrl);
                    }else {
                        if (null != systemType && systemType.length() != 0) {
                            List<String> strings = Splitter.on(",").omitEmptyStrings().splitToList(systemType);
                            strings.stream().filter(s -> s.equals(nodeId.toString())).forEach(s -> tNewsArticleList.remove(tNewsArticle));
                        }
                        if (null != uidS && uidS.length() != 0) {
                            List<String> strings = Splitter.on(",").omitEmptyStrings().splitToList(uidS);
                            strings.stream().filter(s -> s.equals(userId.toString())).forEach(s -> tNewsArticleList.remove(tNewsArticle));
                        }
                        tNewsArticle.setUrl(tNewsArticle.getOutUrl());
                    }
                }
                if (null==newsReq.getCurPage()){
                    newsReq.setCurPage(1L);
                }
                int fromIndex = 10 * (newsReq.getCurPage().intValue() - 1);
                int toIndex = (Math.min((10 * newsReq.getCurPage().intValue()), tNewsArticleList.size()));
                if (fromIndex > toIndex) {
                    fromIndex = toIndex;
                }
                List<TNewsArticle> list1 = tNewsArticleList.subList(fromIndex, toIndex);
                //add cms
                ArrayList<NewsArticleDto> list = new ArrayList<>();
                list1.forEach(i -> {
                    NewsArticleDto dto = new NewsArticleDto();
                    BeanUtils.copyProperties(i, dto);
                    list.add(dto);
                });
                //添加到redis
               /* redisClient.set(""+newsReq.getTid()+RedisConstant.QUERY_LIST_FOR_ARTICLE_KEY+""+newsReq.getCurPage(),list);*/
                //设置过期时间
               /* redisClient.expire(""+newsReq.getTid()+RedisConstant.QUERY_LIST_FOR_ARTICLE_KEY+""+newsReq.getCurPage(), 60 * 60);*/
                result.setMsg("请求成功");
                result.setResult(list);
                result.setCode(GlobalConstant.ResponseCode.SUCCESS);
                //logger.info("数据库");
                return result;
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
                result.setMsg("网络异常，请稍后重试");
                return result;
            }
    }

    //根据id更新文章的浏览次数
    @Override
    @Transactional
    public ResponseResult updateHits(int id) {
        ResponseResult result=new ResponseResult();
        Integer num = tNewsArticleMapper.updateHits(id);
        if (num==1){
        result.setMsg("更新成功");
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        result.setResult(num);
            return result;
        }else {
            result.setMsg("更新失败，参数有误");
            result.setCode(GlobalConstant.ResponseCode.FAIL);
            return result;
        }
    }

    @Autowired
    private TLikeMapper tLikeMapper;

    //根据id获取文章的详细信息
    @Override
    @Transactional
    public ResponseResult queryOne(TLike tLike) {
        ResponseResult<Object> result = new ResponseResult<>();
        String pre = pre();
        if (null==tLike.getNid() || null==tLike.getUid()){
            result.setMsg("参数错误，请检查参数");
            logger.info("参数错误");
            return result;
        }
        TNewsArticle tNewsArticle  =tNewsArticleMapper.queryOne(tLike.getNid().longValue());
        if (null == tNewsArticle) {
            result.setMsg("暂无数据");
            return result;
        }
        //获取到新闻详情之后要看到这个用户是否给这篇新闻点赞了
        TLike tLike1 = tLikeMapper.queryLike(tLike.getUid(), tLike.getNid());
        if (tLike1==null){
            tLikeMapper.insertLike(tLike.getUid(), tLike.getNid());
            TLike tLike2 = tLikeMapper.queryLike(tLike.getUid(), tLike.getNid());
            tLike2.getStatus();
            String url = tNewsArticle.getUrl();
            String newUrl = pre + url;
            tNewsArticle.setUrl(newUrl);
            result.setCode(GlobalConstant.ResponseCode.SUCCESS);
            result.setResult(tNewsArticle);
            result.setStatus(tLike2.getStatus()+"");
            result.setMsg("请求成功");
            return result;
        }
        String url = tNewsArticle.getUrl();
        String newUrl = pre + url;
        tNewsArticle.setUrl(newUrl);
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        result.setResult(tNewsArticle);
        result.setStatus(tLike1.getStatus()+"");
        result.setMsg("请求成功");
        return result;
    }

}
