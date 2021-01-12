package com.xinlian.member.server.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.xinlian.biz.model.TVersion;
import com.xinlian.common.enums.ErrorCode;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.jwt.annotate.PassToken;
import com.xinlian.member.biz.service.TVersionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wjf
 * @since 2020-01-02
 */
@RestController
@RequestMapping("/{versionPath}/tVersion")
@Api("app版本接口")
public class TVersionController {

    @Autowired
    private TVersionService tVersionService;
    @PostMapping("getVersion")
    @ApiOperation("获取版本号")
    @PassToken
    public ResponseResult getVersion(){
        return tVersionService.queryOne();
    }

    @PostMapping("insertVersion")
    @ApiOperation("新增版本")
    public ResponseResult insertVersion(@RequestBody TVersion tVersion){
        tVersionService.insert(tVersion);
        return ResponseResult.builder().msg(ErrorCode.REQ_SUCCESS.getDes()).code(ErrorCode.REQ_SUCCESS.getCode()).build();
    }

    @PostMapping("getAllVersion")
    @ApiOperation("获取所有历史版本")
    public ResponseResult getAllVersion(){
        List<TVersion> tVersions = tVersionService.selectList(new EntityWrapper<TVersion>().orderBy("createdTime"));
        return ResponseResult.builder().msg(ErrorCode.REQ_SUCCESS.getDes()).code(ErrorCode.REQ_SUCCESS.getCode()).result(tVersions).build();
    }
}

