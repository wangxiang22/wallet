package com.xinlian.admin.server.controller;

import com.github.pagehelper.PageInfo;
import com.xinlian.admin.biz.service.AccountCheckService;
import com.xinlian.admin.biz.service.HomePageService;
import com.xinlian.admin.server.controller.handler.StatisticsCurrencyEveryDayDataPlugTaskHandler;
import com.xinlian.admin.server.vo.AccountCheckVo;
import com.xinlian.admin.server.vo.AccountCheckVoConvertor;
import com.xinlian.biz.model.AccountCheckModel;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.response.ResponseResultPage;
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

import java.util.List;
import java.util.Map;

@Api(value = "财务核查接口")
@RestController
@RequestMapping("/financeCheck")
@Slf4j
public class StatisticsCurrencyDataPlugController {


    @Autowired
    private HomePageService homePageService;
    @Autowired
    private AccountCheckService accountCheckService;
    @Autowired
    private StatisticsCurrencyEveryDayDataPlugTaskHandler statisticsCurrencyEveryDayDataPlugTaskHandler;

    @ApiOperation(value = "财务核查-币种相关数据")
    @GetMapping(value = "/v1/walletData")
    public ResponseResult walletData(){
        long start = System.currentTimeMillis();
        try {
            Map map = homePageService.getStatisticsCurrencyNum();
            log.info("执行完方法时间：{}", System.currentTimeMillis()-start );
            return new ResponseResult(map);
        } catch (Exception e) {
            log.error("获取财务核查-币种相关数据数据异常:{}", e.toString(), e);
            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
        }
    }

    @ApiOperation(value = "财务核查-币种对账数据")
    @GetMapping(value = "/v1/accountCheck/list")
    public ResponseResultPage accountCheckList(
            @ApiParam(value = AccountCheckModel.params)
            @RequestParam Map<String,Object> searchParams){
        try {
            Long startTime = System.currentTimeMillis();
            PageInfo pageInfo = accountCheckService.queryPage(searchParams);
            List<AccountCheckVo> vos = new AccountCheckVoConvertor().convertList(pageInfo.getList());
            pageInfo.setList(vos);
            log.info("~~~~~~:财务核查-币种对账数据花费时间：{}",System.currentTimeMillis()-startTime);
            return new ResponseResultPage(pageInfo);
        } catch (Exception e) {
            log.error("获取币种相关数据异常:{}", e.toString(), e);
            return new ResponseResultPage(false);
        }
    }

    @ApiOperation(value = "财务核查-数据来往汇总-all节点")
    @GetMapping(value = "/v1/updateAccountCheckData")
    public ResponseResult updateAccountCheckData(@RequestParam String passKey,@RequestParam(required=false) String clearDay){
        try {
            if(!"passKey20200423".equals(passKey)){return new ResponseResult(new BizException("没有对应执行权限!"));}
            statisticsCurrencyEveryDayDataPlugTaskHandler.doScheduledUpdateTradeOrder(clearDay);
            return new ResponseResult(true);
        } catch (Exception e) {
            log.error("获取币种相关数据异常:{}", e.toString(), e);
            return new ResponseResult(false);
        }
    }


    @ApiOperation(value = "财务核查-汇总-顶级节点")
    @GetMapping(value = "/v1/summaryTopNodeData")
    public ResponseResult summaryTopNodeData(@RequestParam String passKey,@RequestParam(required=false) String clearDay){
        try {
            if(!"passKey20200425".equals(passKey)){return new ResponseResult(new BizException("没有对应执行权限!"));}
            statisticsCurrencyEveryDayDataPlugTaskHandler.summaryTopNodeData(clearDay);
            return new ResponseResult(true);
        } catch (Exception e) {
            log.error("获取币种相关数据异常:{}", e.toString(), e);
            return new ResponseResult(false);
        }
    }


}
