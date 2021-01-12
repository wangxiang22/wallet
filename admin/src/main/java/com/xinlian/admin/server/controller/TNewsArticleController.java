package com.xinlian.admin.server.controller;

import com.xinlian.admin.biz.service.TNewsArticleService;
import com.xinlian.admin.server.operationLog.OpeAnnotation;
import com.xinlian.biz.model.TNewsArticle;
import com.xinlian.common.enums.OperationModuleEnum;
import com.xinlian.common.enums.OperationTypeEnum;
import com.xinlian.common.request.NewsReq;
import com.xinlian.common.request.PageReq;
import com.xinlian.common.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/tNewsArticle")
@Api("新闻相关")
public class TNewsArticleController {

    @Autowired
    private TNewsArticleService tNewsArticleService;

    //后台相关
    @ApiOperation("展现所有新闻相关,banner图，文章，帮助中心，通知里面的")
    @PostMapping("queryAll")
    public ResponseResult queryAll(@RequestBody PageReq pageReq) {
        return tNewsArticleService.queryAll( pageReq);
    }

    @OpeAnnotation(modelName = OperationModuleEnum.NEWS_MANAGE,typeName = OperationTypeEnum.OTHER_OPERATE, opeDesc = "修改新闻")
    @ApiOperation("修改新闻 ,根据id的修改，id必须传过来")
    @PostMapping("updateNews")
    public ResponseResult updateNews(@RequestBody TNewsArticle tNewsArticle){
        return tNewsArticleService.updateNews(tNewsArticle);
    }

    @OpeAnnotation(modelName = OperationModuleEnum.NEWS_MANAGE,typeName = OperationTypeEnum.OTHER_OPERATE, opeDesc = "新增新闻")
    @ApiOperation("新增一条数据，tid: 1 banner 3 文章 4 隐藏新闻 5 站内新闻 6 新手须知 7 常见问题 8 软件操作 9 通知 " +
            " system_type:不传是全部节点可以看到，传入哪个，哪个就看不到  type_language：中文传 :CN 英文传: EN   uids：" +
            "传那些uid进来，哪些就可以看到通知 只针对通知")
    @PostMapping("insertOne")
    public ResponseResult insertOne(@RequestBody TNewsArticle tNewsArticle) {
        return tNewsArticleService.insertOne(tNewsArticle);
    }

    @OpeAnnotation(modelName = OperationModuleEnum.NEWS_MANAGE,typeName = OperationTypeEnum.OTHER_OPERATE, opeDesc = "删除新闻")
    @ApiOperation("根据id 删除 ")
    @PostMapping("deleteByID")
    public ResponseResult deleteByID(@RequestBody NewsReq newsReq) {
        return tNewsArticleService.deleteById(newsReq.getId());
    }
    @ApiOperation("根据id查询对应文章")
    @PostMapping("queryOne")
    public ResponseResult queryOne(@RequestBody NewsReq newsReq) {
        return tNewsArticleService.queryOne(newsReq.getId());
    }

    @ApiOperation("搜索,默认一页10条数据")
    @PostMapping("searchNews")
    public ResponseResult searchNews(@RequestBody NewsReq newsReq) {
        return tNewsArticleService.searchNews(newsReq);
    }




}


