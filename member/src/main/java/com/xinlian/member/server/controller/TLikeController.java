package com.xinlian.member.server.controller;


import com.xinlian.common.request.LikeReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.jwt.annotate.PassToken;
import com.xinlian.member.biz.service.TLikeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wx
 * @since 2020-03-27
 */
@RestController
@RequestMapping("/{versionPath}/tLike")
public class TLikeController {

    @Autowired
    private TLikeService tLikeService;

    @ApiOperation("查询用户是否给某条新闻点赞")
    @PostMapping("queryLike")
    @PassToken
    public ResponseResult queryLike(@RequestBody LikeReq likeReq) {
        return tLikeService.queryLike(likeReq);
    }
}

