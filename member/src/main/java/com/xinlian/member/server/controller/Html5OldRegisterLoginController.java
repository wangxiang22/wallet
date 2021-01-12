package com.xinlian.member.server.controller;


import com.xinlian.biz.utils.NodeVoyageUtil;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.enums.SendRegisterTypeEnum;
import com.xinlian.common.request.RegisterReq;
import com.xinlian.common.request.ServerNodeReq;
import com.xinlian.common.response.CountryDicRes;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import com.xinlian.common.utils.DateFormatUtil;
import com.xinlian.member.biz.alisms.util.SmsUtil;
import com.xinlian.member.biz.jwt.annotate.PassToken;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.redis.RedisConstant;
import com.xinlian.member.biz.service.IRegisterLoginService;
import com.xinlian.member.biz.service.IServerNodeService;
import com.xinlian.member.server.controller.handler.CheckSmsRuleHandler;
import com.xinlian.member.server.controller.handler.LimitWithdrawHandler;
import com.xinlian.member.server.controller.handler.RegisterHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

@Api(value = "h5原注册登录")
@Controller
@Slf4j
public class Html5OldRegisterLoginController {

    @Autowired
    private IRegisterLoginService registerLoginService;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private IServerNodeService serverNodeService;
    @Autowired
    private RedisLockRegistry redisLockRegistry;
    @Autowired
    private NodeVoyageUtil nodeVoyageUtil;
    @Autowired
    private RegisterHandler registerHandler;
    @Autowired
    private LimitWithdrawHandler limitWithdrawHandler;
    @Autowired
    private CheckSmsRuleHandler checkSmsRuleHandler;

    @ApiOperation(value = "发送注册短信-校验是否可注册等逻辑", httpMethod = "POST")
    @RequestMapping(value = "/register/sms/send", method = RequestMethod.POST)
    @ResponseBody
    @PassToken
    @CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
    public ResponseResult sendRegisterSms(@RequestBody Map<String,  String> paramMap){
        RegisterReq req = registerHandler.decodeDataToObject(paramMap);
        if(1!=req.getType()){
            throw new BizException("参数不正确!");
        }
        //检验这个手机号码是否具有资格访问这个接口
        checkSmsRuleHandler.doCheckSmsRuleHandler(SmsUtil.getCountryCodeAndPhone(req.getPhone(),req.getCountryCode()),req.getType()+"");
        boolean lockFlag = true;
        Lock lock = redisLockRegistry.obtain(req.getPhone()+req.getNodeId());
        try {
            if(!lock.tryLock()){
                lockFlag = false;
                throw new BizException("短信发送中,请耐心等待!");
            }
            //验证节点是否在限制集合里面
            limitWithdrawHandler.doLimitServerNodeId(req);
            boolean isInland = true;
            if(nodeVoyageUtil.belongVoyageNode(req.getNodeId())){
                if(86==req.getCountryCode()){
                    throw new BizException("请确定国家区号是否正确!");
                }
                String isCheckInlandFlag = redisClient.get(RedisConstant.APP_REDIS_PREFIX + "IS_CHECK_INLAND_FLAG");
                //验证手机号
                if(null!=isCheckInlandFlag && req.getPhone().length()==11 && "1".equals(req.getPhone().substring(0,1))){
                    throw new BizException("请确认手机号码!");
                }
                //航海计划节点
                registerHandler.judgeAbroadNodeIsRegister(req);
                isInland = false;
            }
            return registerLoginService.sendRegisterSms(req, isInland);
        }catch (BizException e){
            log.error(DateFormatUtil.get(7,new Date())+"发送注册短信出现业务异常：{}",e.getMsg(),e);
            return new ResponseResult(e);
        }catch (Exception e){
            log.error(DateFormatUtil.get(7,new Date())+"发送注册短信出现业务异常：{}",e.toString(),e);
            return new ResponseResult(new BizException("请稍后重试!"));
        }finally {
            if(lockFlag){
                lock.unlock();
            }
        }
    }

    @ApiOperation(value = "查询所有节点信息",httpMethod = "GET")
    @GetMapping("/serverNode/findNodeListByStatus")
    @ResponseBody
    @PassToken
    public ResponseResult findNodeListByStatus(@RequestParam(name = "node_type", required = false) Integer node_type,
                                               @RequestParam(name = "parentId", required = false) Long parentId,
                                               @RequestParam(name = "id", required = false) Long id) {
        try {
            if (null != node_type) {
                return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).msg("请下载最新APP！！！").build();
            }
            return serverNodeService.findNodeListByStatus();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).build();
        }
    }

    @ApiOperation(value = "国家列表", httpMethod = "GET")
    @GetMapping(value = "/register/country/list")
    @PassToken
    @ResponseBody
    @CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
    public ResponseResult<List<CountryDicRes>> findCountryDic(){
        return registerLoginService.findCountryDic();
    }

    @ApiOperation(value = "注册", httpMethod = "POST")
    @RequestMapping(value = "/register/register", method = RequestMethod.POST)
    @ResponseBody
    @PassToken
    @CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
    public ResponseResult register(@RequestBody Map<String,String> paramMap){
        try {
            //check ip register much
            registerHandler.checkRegisterFromIpDoMuch();
            RegisterReq registerReq = registerHandler.decodeDataToObject(paramMap);
            //检验这个手机号码是否具有资格访问这个接口
            checkSmsRuleHandler.doCheckSmsRuleHandler(SmsUtil.getCountryCodeAndPhone(registerReq.getPhone(),registerReq.getCountryCode()), SendRegisterTypeEnum.REGISTER.getType()+"");
            registerReq.checkParam();
            //验证节点是否在限制集合里面
            limitWithdrawHandler.doLimitServerNodeId(registerReq);
            boolean isInland = true;
            if(nodeVoyageUtil.belongVoyageNode(registerReq.getNodeId())){
                //航海计划节点
                registerHandler.judgeAbroadNodeIsRegister(registerReq);
                isInland = false;
            }
            return registerLoginService.register(registerReq,isInland);
        }catch (BizException e){
            log.error(DateFormatUtil.get(7,new Date())+"注册出现业务异常:{}",e.getMsg(),e);
            return new ResponseResult(e);
        }catch (Exception e){
            log.error(DateFormatUtil.get(7,new Date())+"注册出现异常:{}",e.toString(),e);
            return new ResponseResult(new BizException("注册出现异常,请稍后重试!"));
        }
    }

}
