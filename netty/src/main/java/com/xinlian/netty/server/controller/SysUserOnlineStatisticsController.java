package com.xinlian.netty.server.controller;

import com.xinlian.common.response.OnlineRankStatisticsVO;
import com.xinlian.common.response.OnlineStatisticsVO;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.netty.biz.service.SysUserOnlineStatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangJun
 * @version V1.0  2020/4/26
 **/

@Api(value = "用户在线统计")
@RestController
@RequestMapping(value = "/sysStatistics")
public class SysUserOnlineStatisticsController {

    @Autowired
    private SysUserOnlineStatisticsService sysUserOnlineStatisticsService;

    @ApiOperation(value = "用户在线时长统计",httpMethod = "GET")
    @GetMapping("/onlineStatistics")
    public ResponseResult<OnlineStatisticsVO> onlineStatistics(){
        return sysUserOnlineStatisticsService.onlineStatistics();
    }


    @ApiOperation(value = "用户在线排行",httpMethod = "GET")
    @GetMapping("/onlineRank")
    public ResponseResult<OnlineRankStatisticsVO> onlineRank(){
        return sysUserOnlineStatisticsService.onlineRank();
    }



}
