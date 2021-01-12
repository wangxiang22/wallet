package com.xinlian.member.server.controller;


import com.xinlian.biz.model.TRecommend;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.jwt.util.JwtUtil;
import com.xinlian.member.biz.service.TRecommendService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wx
 * @since 2020-03-25
 */
@RestController
@RequestMapping("/{versionPath}/tRecommend")
public class TRecommendController {

    @Autowired
    private TRecommendService tRecommendService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private HttpServletRequest request;


    @ApiOperation("查看新闻模块上面的推荐专区")
    @PostMapping("queryRecommend")
    public ResponseResult<List<TRecommend>> queryRecommend() {
        Long nodeId = jwtUtil.getNodeId(request);
        return tRecommendService.queryRecommend(nodeId);
    }
}

