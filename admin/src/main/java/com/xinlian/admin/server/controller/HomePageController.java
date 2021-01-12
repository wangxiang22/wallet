package com.xinlian.admin.server.controller;

import com.xinlian.admin.biz.service.HomePageService;
import com.xinlian.admin.biz.service.UserInfoRegisterService;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import com.xinlian.common.result.ErrorInfoEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * com.xinlian.admin.server.controller
 *
 * @date 2020/2/17 10:55
 */
@Api(value = "首页接口提供")
@RestController
@RequestMapping("/index")
@Slf4j
public class HomePageController {

    @Autowired
    private HomePageService homePageService;
    @Autowired
    private UserInfoRegisterService userInfoRegisterService;

    @ApiOperation(value = "首页-相关注册数据")
    @GetMapping(value = "/v1/infoData")
    public ResponseResult indexData(
            @ApiParam(name = "isForceRefresh",required = true,value = "isForceRefresh = true:强制更新；false:取redis信息")
            @RequestParam boolean isForceRefresh){
        try {
            return new ResponseResult(homePageService.getIndexData(isForceRefresh));
        } catch (Exception e) {
            log.error("获取首页数据异常:{}", e.toString(), e);
            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
        }
    }

    @ApiOperation(value = "首页-钱包金额数据")
    @GetMapping(value = "/v1/walletData")
    public ResponseResult walletData(
            @ApiParam(name = "isForceRefresh",required = true,value = "isForceRefresh = true:强制更新；false:取redis信息")
            @RequestParam boolean isForceRefresh,
            @ApiParam(name = "currencyCode",required = true,value = "currencyCode = USDT/CAT/CAG/GPT")
            @RequestParam String currencyCode){
        try {
            return new ResponseResult(homePageService.getPlatformWalletData(isForceRefresh,currencyCode));
        } catch (Exception e) {
            log.error("获取首页钱包金额数据异常:{}", e.toString(), e);
            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
        }
    }

    @ApiOperation(value = "首页-节点注册排名接口")
    @GetMapping(value = "/v1/serverNodeRegisterRanking")
    public ResponseResult serverNodeRegisterRanking(
            @ApiParam(name = "isForceRefresh",required = true,value = "isForceRefresh = true:强制更新；false:取redis信息")
            @RequestParam boolean isForceRefresh,
            @ApiParam(name = "startDate",required = false,value = "startDate 开始日期")
            @RequestParam(required = false) String startDate,
            @ApiParam(name = "endDate",required = false,value = "endDate 结束日期")
            @RequestParam(required = false) String endDate){
        try{
            if(startDate!=null){
                startDate = startDate.trim() + " 00:00:00";
            }
            if(endDate!=null){
                endDate = endDate.trim() + " 23:59:59";
            }
            return new ResponseResult(userInfoRegisterService.serverNodeRegisterRanking(isForceRefresh,startDate,endDate));
        }catch (Exception e){
            log.error("首页-节点注册排名接口出现异常:{}", e.toString(), e);
            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
        }
    }

    @ApiOperation(value = "首页-节点激活排名接口")
    @GetMapping(value = "/v1/serverNodeActivateRanking")
    public ResponseResult serverNodeActivateRanking(
            @ApiParam(name = "isForceRefresh",required = true,value = "isForceRefresh = true:强制更新；false:取redis信息")
            @RequestParam boolean isForceRefresh,
            @ApiParam(name = "startDate",required = false,value = "startDate 开始日期")
            @RequestParam(required = false) String startDate,
            @ApiParam(name = "endDate",required = false,value = "endDate 结束日期")
            @RequestParam(required = false) String endDate){
        try{
            //转换时间范围
            if(startDate!=null){
                startDate = startDate.trim() + " 00:00:00";
            }
            if(endDate!=null){
                endDate = endDate.trim() + " 23:59:59";
            }
            return new ResponseResult(userInfoRegisterService.serverNodeActivateRanking(isForceRefresh,startDate,endDate));
        }catch (Exception e){
            log.error("首页-节点激活排名接口出现异常:{}", e.toString(), e);
            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
        }
    }

    @ApiOperation(value = "首页-新用户走势接口")
    @GetMapping(value = "/v1/newCustomerTrend")
    public ResponseResult newCustomerTrend(
            @ApiParam(name = "isForceRefresh",required = true,value = "isForceRefresh = true:强制更新；false:取redis信息")
            @RequestParam boolean isForceRefresh,
            @ApiParam(name = "dimensionsType",required = true,value = "dimensionsType 统计维度：年(YEAR)、月(MONTH)、周(WEEK)、当天(TODAY)")
            @RequestParam String dimensionsType){
        try{
            return new ResponseResult(userInfoRegisterService.newCustomerTrend(isForceRefresh,dimensionsType));
        }catch (Exception e){
            log.error("首页-新用户走势接口出现异常:{}", e.toString(), e);
            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
        }
    }
}
