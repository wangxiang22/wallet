package com.xinlian.member.biz.service.impl;

import com.xinlian.biz.dao.TNewsArticleMapper;
import com.xinlian.biz.model.TLike;
import com.xinlian.biz.dao.TLikeMapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.xinlian.biz.model.TNewsArticle;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.request.LikeReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.service.TLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wx
 * @since 2020-03-27
 */
@Service
public class TLikeServiceImpl extends ServiceImpl<TLikeMapper, TLike> implements TLikeService {

    @Autowired
    private TLikeMapper tLikeMapper;

    @Autowired
    private TNewsArticleMapper tNewsArticleMapper;

    @Transactional
    @Override
    public ResponseResult queryLike(LikeReq likeReq) {
        ResponseResult result =new ResponseResult();
        if(null==likeReq.getNid() || "".equals(likeReq.getNid().longValue())){
            result.setMsg("参数有误请检查参数");
            return result;
        }
        TLike tLike= tLikeMapper.queryLike(likeReq.getUid(), likeReq.getNid());
        TNewsArticle tNewsArticle = tNewsArticleMapper.queryOne(likeReq.getNid().longValue() );
         if (null==tNewsArticle || "".equals(tNewsArticle)){
             result.setMsg("没有这条新闻");
             return result;
         }
        if (null==tLike){
            //没找到这条点赞记录，添加一条记录，点赞状态是1
         tLikeMapper.insertLike1(likeReq.getUid(), likeReq.getNid());
            TLike tLike1= tLikeMapper.queryLike(likeReq.getUid(), likeReq.getNid());
            //跟新新闻中的总数
            Long likes = tNewsArticle.getLikes();
            tNewsArticleMapper.updateLikes(likes+1L,likeReq.getNid().longValue());
            result.setMsg("请求成功");
            result.setCode(GlobalConstant.ResponseCode.SUCCESS);
           result.setResult(tLike1);
            return result;
        }else {
            if (tLike.getStatus()==1){
                //如果点赞状态是1取消点赞，更新总数减一，把点赞状态改为0
                tLikeMapper.updateLikeStatus(likeReq.getUid(), likeReq.getNid());
                    tNewsArticleMapper.updateLikes(tNewsArticle.getLikes()-1L,likeReq.getNid().longValue());
                    TLike tLike1= tLikeMapper.queryLike(likeReq.getUid(), likeReq.getNid());
                    System.out.println(tNewsArticle.getLikes());
                    result.setMsg("请求成功");
                    result.setCode(GlobalConstant.ResponseCode.SUCCESS);
                    result.setResult(tLike1);
                    return result;
            }
            //找到了点赞记录，并且取消点赞，又想点赞，更新总数加一，点赞状态改为1
            tLikeMapper.updateLike(likeReq.getUid(), likeReq.getNid());
            tNewsArticleMapper.updateLikes(tNewsArticle.getLikes()+1L,likeReq.getNid().longValue());
            TLike tLike1= tLikeMapper.queryLike(likeReq.getUid(), likeReq.getNid());
            //System.out.println(tNewsArticle.getLikes());
            result.setMsg("请求成功");
            result.setCode(GlobalConstant.ResponseCode.SUCCESS);
            result.setResult(tLike1);
            return result;
        }
    }
}
