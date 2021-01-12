package com.xinlian.member.server.controller;

import com.xinlian.biz.model.TLike;
import com.xinlian.common.request.NewsReq;
import com.xinlian.common.request.PageReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.jwt.annotate.PassToken;
import com.xinlian.member.biz.optionsconfig.ActivityConfig;
import com.xinlian.member.biz.service.TNewsArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/{versionPath}/tNewsArticle")
@Api("新闻相关")
public class TNewsArticleController {

    @Autowired
    private TNewsArticleService tNewsArticleService;
    @Autowired
    private ActivityConfig activityConfig;

    @ApiOperation("根据id查询对应文章 ,参数为新闻id和用户id")
    @PostMapping("queryOne")
    @PassToken
    public ResponseResult queryOne(@RequestBody TLike tLike) {
        return tNewsArticleService.queryOne(tLike);
    }

    @ApiOperation("获取底部导航显示数据")
    @PostMapping("queryActivity")
    public ResponseResult queryActivity() {
        return tNewsArticleService.queryActivity(activityConfig);
    }

    @ApiOperation("所有通知，带分页，区分节点 type,1是中文，2是英文 curPage：第几页，一般第1页就行了，通知的节点区分和uid两者只存在一个，写了给节点看的，下次想给uid的看就把之前的代替掉")
    @PostMapping("queryAllInform")
    public ResponseResult queryAllInform(@RequestBody PageReq pageReq) {
        return tNewsArticleService.queryInform(pageReq);
    }

    @ApiOperation(" type:1是中文，2是英文，不传默认是中文  tid:根据新闻分类来传，就是新闻分类的id")
    @PostMapping("searchNewsAll")
    public ResponseResult searchNewsAll(@RequestBody NewsReq newsReq) {
        return tNewsArticleService.searchNewsAll(newsReq);
    }

    @ApiOperation("根据id更新浏览次数")
    @PostMapping("updateHits")
    public ResponseResult updateHits(@RequestBody NewsReq newsReq) {
        return tNewsArticleService.updateHits(newsReq.getId().intValue());
    }

}


