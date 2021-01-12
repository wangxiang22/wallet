package com.xinlian.admin.server.controller;


import com.xinlian.admin.biz.service.TRecommendService;
import com.xinlian.biz.model.TRecommend;
import com.xinlian.common.response.ResponseResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wx
 * @since 2020-03-26
 */
@RestController
@RequestMapping("/tRecommend")
public class TRecommendController {

    @Autowired
    private TRecommendService tRecommendService;


    @ApiOperation("删除新闻模块上面的推荐专区")
    @PostMapping("deleteRecommend")
    public ResponseResult deleteRecommend(int id) {
        return tRecommendService.deleteRecommend(id);
    }

    @ApiOperation("查看新闻模块上面的推荐专区")
    @PostMapping("queryRecommend")
    public ResponseResult queryRecommend() {
        return tRecommendService.queryRecommend();
    }

    @ApiOperation("修改新闻模块上面的推荐专区")
    @PostMapping("updateRecommend")
    public ResponseResult updateRecommend(@RequestBody TRecommend tRecommend) {
        return tRecommendService.updateRecommend(tRecommend);
    }

    @ApiOperation("新增新闻模块上面的推荐专区")
    @PostMapping("insertRecommend")
    public ResponseResult insertRecommend(@RequestBody TRecommend tRecommend) {
        return tRecommendService.insertRecommend(tRecommend);
    }
}

