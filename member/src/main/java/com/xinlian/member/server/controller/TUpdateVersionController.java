package com.xinlian.member.server.controller;


import com.xinlian.common.redis.RedisKeys;
import com.xinlian.common.request.VersionDataReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.utils.CommonUtil;
import com.xinlian.common.utils.SystemUtils;
import com.xinlian.member.biz.jwt.annotate.PassToken;
import com.xinlian.member.biz.redis.LuaScriptRedisService;
import com.xinlian.member.biz.redis.RedisConstant;
import com.xinlian.member.biz.service.TUpdateVersionService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author WX
 * @since 2020-04-27
 */
@RestController
@RequestMapping("/{versionPath}/tUpdateVersion")
@Slf4j
public class TUpdateVersionController {

    @Autowired
    TUpdateVersionService updateVersionService;
    @Autowired
    private LuaScriptRedisService luaScriptRedisService;

    @ApiOperation("获取新版本")
    @PostMapping("queryVersion")
    @PassToken
    public ResponseResult queryVersion( @RequestBody VersionDataReq versionReq){
        return updateVersionService.queryVersion(versionReq);
    }

    @ApiOperation("更新推送过去的数量到redis，参数 type: 1 ,安卓，：2，ios。id为本条更新版本的id")
    @PostMapping("updateIssued")
    @PassToken
    public ResponseResult updateIssued(HttpServletRequest request,@RequestBody VersionDataReq versionReq) {
        String ipAddress = SystemUtils.getIpAddress(request);
        String redisKey = RedisKeys.appVersionIssuedKey(RedisConstant.VERSION_COUNT_ISSUED,versionReq.getId(),versionReq.getType());
        luaScriptRedisService.doHashIncr(redisKey,ipAddress, CommonUtil.getTheDayResidueSecond(),1L);
        return new ResponseResult("请求成功");
    }


    @ApiOperation("更新下载数量到redis，参数 type: 1 ,安卓，：2，ios。id为本条更新版本的id")
    @PostMapping("updateInstall")
    @PassToken
    public ResponseResult updateInstall(HttpServletRequest request,@RequestBody VersionDataReq versionReq) {
        String ipAddress = SystemUtils.getIpAddress(request);
        String redisKey = RedisKeys.appVersionIssuedKey(RedisConstant.VERSION_COUNT_INSTALL,versionReq.getId(),versionReq.getType());
        luaScriptRedisService.doHashIncr(redisKey,ipAddress, CommonUtil.getTheDayResidueSecond(),1L);
        return new ResponseResult("请求成功");
    }



    @ApiOperation("定时更新推送过去的数量和下载的数量到数据库")
    @PostMapping("updateIssuedToSql")
    @PassToken
    public ResponseResult updateIssuedToSql() {
        return updateVersionService.updateIssuedToSql();
    }


    }

