package com.xinlian.member.biz.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.xinlian.biz.dao.AdminOptionsMapper;
import com.xinlian.biz.dao.TUserInfoMapper;
import com.xinlian.biz.model.TServerNode;
import com.xinlian.biz.model.TUserInfo;
import com.xinlian.biz.utils.AdminOptionsUtil;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
import com.xinlian.common.redis.RedisKeys;
import com.xinlian.common.request.BindMobileReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.response.SystemSwitchRes;
import com.xinlian.common.result.BizException;
import com.xinlian.common.result.ErrorInfoEnum;
import com.xinlian.member.biz.alisms.util.SmsUtil;
import com.xinlian.member.biz.chuanglan.util.ChuangLanSmsService;
import com.xinlian.member.biz.jwt.util.EncryptionUtil;
import com.xinlian.member.biz.redis.LuaScriptRedisService;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.redis.RedisConstant;
import com.xinlian.member.biz.service.IServerNodeService;
import com.xinlian.member.biz.service.MobileBindService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MobileBindServiceImpl implements MobileBindService {
    @Autowired
    private TUserInfoMapper tUserInfoMapper;
    @Autowired
    private IServerNodeService serverNodeService;
    @Autowired
    private AdminOptionsMapper adminOptionsMapper;
    @Autowired
    private LuaScriptRedisService luaScriptRedisService;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private ChuangLanSmsService chuangLanSmsService;
    @Autowired
    private AdminOptionsUtil adminOptionsUtil;

    @Override
    public ResponseResult findMobileExists(BindMobileReq bindMobileReq) {
        //默认限制手机数量
        int mobileCount = 5;
        //默认搜索条件
        EntityWrapper<TUserInfo> wrapper = new EntityWrapper<>();
        wrapper.eq("mobile", bindMobileReq.getMobile());
        //判断全局开关是否打开，开了则走全局配置，没开就走节点单独配置
        try {
            SystemSwitchRes systemSwitchRes =
                    adminOptionsUtil.fieldEntityObject(AdminOptionsBelongsSystemCodeEnum.SYSTEM_SWITCH.getBelongsSystemCode(), SystemSwitchRes.class);
            if (null != systemSwitchRes && "1".equals(systemSwitchRes.getSystemApplicationFlag())) {
                mobileCount = Integer.parseInt(systemSwitchRes.getMobileRegisterAmount());
            }else {
                TServerNode tServerNode = serverNodeService.getById(bindMobileReq.getNodeId());
                mobileCount = tServerNode.getMobileRegisterAmount();
                //0：5个，非0：填写的数字
                mobileCount = mobileCount == 0 ? 5 : mobileCount;
                //带上同节点条件
                wrapper.eq("server_node_id", bindMobileReq.getNodeId());
            }
        } catch (Exception e) {
            log.error("查找全局配置出现业务异常：{}",e.toString(),e);
            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
        }
        int count = tUserInfoMapper.selectCount(wrapper);
        if(count >= mobileCount){
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL)
                    .msg("该手机号已绑定" + mobileCount + "个账户，请更换手机号").build();
        }
        return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).build();
    }

    /**
     * @see RedisKeys#createSmsPhoneKey 813
     * @param bindMobileReq
     * @return
     */
    @Override
    public ResponseResult bindMobile(BindMobileReq bindMobileReq) {
        String mobile = bindMobileReq.getCountryCode()==86 ? bindMobileReq.getMobile() : bindMobileReq.getCountryCode() + bindMobileReq.getMobile();
        String mobileCode = redisClient.get(SmsUtil.createPhoneKey(RedisKeys.createSmsPhoneKey(813,mobile)));
        if(!bindMobileReq.getCode().equals(mobileCode)){
            log.error("redis中的value值:"+mobileCode+"接收到的参数code"+bindMobileReq.getCode());
            throw new BizException("您输入的验证码不正确或验证码已过期!");
        }
        TUserInfo tUserInfo = new TUserInfo();
        tUserInfo.setUid(bindMobileReq.getUid());
        tUserInfo.setMobile(bindMobileReq.getMobile());
        tUserInfo.setCountryCode(bindMobileReq.getCountryCode());
        Integer updateResult = tUserInfoMapper.updateById(tUserInfo);
        if (0 == updateResult) {
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).msg("绑定手机号失败").build();
        }
        return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).build();
    }

    @Override
    public ResponseResult sendMobileSms(BindMobileReq bindMobileReq) {
        bindMobileReq.setType(13);
        String mobileSendNumKey = RedisConstant.APP_REDIS_PREFIX+"MOBILE_SEND_NUM";
        String mobileInTheTimeKey = RedisConstant.APP_REDIS_PREFIX+"MOBILE_IN_THE_TIME";
        Integer mobileSendNum = getAdminOptionsValue(mobileSendNumKey);
        Integer mobileInTheTime = getAdminOptionsValue(mobileInTheTimeKey);//单位：天
        if(null!=mobileSendNum && null!=mobileInTheTime){
            String incrKey = RedisConstant.APP_REDIS_PREFIX + bindMobileReq.getMobile() + "_" + bindMobileReq.getType() + "_" + bindMobileReq.getNodeId();
            Integer incrNum = redisClient.get(incrKey);
            if(null != incrNum && incrNum > mobileSendNum) {
                return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).msg("您修改次数达到上限请联系客服").build();
            }
        }
        String code = EncryptionUtil.getRandomCode(6);
        String mobile = bindMobileReq.getCountryCode()==86 ? bindMobileReq.getMobile() : bindMobileReq.getCountryCode() + bindMobileReq.getMobile();
        boolean flag = false;
        //国内短信
        String smsRedisKey = pushCodeToRedis(RedisKeys.createSmsPhoneKey(bindMobileReq.getType(), bindMobileReq.getMobile()), code);
        if(bindMobileReq.getCountryCode() == 86){
            flag = chuangLanSmsService.sendRegisterCodeCh(mobile, code,null,smsRedisKey);
        }else{
            //国际短信
            flag = chuangLanSmsService.sendRegisterCodeInte(mobile, code,null,smsRedisKey);
        }
        if(!flag){
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).msg("发送验证码过于频繁，请稍后重试!").build();
        }
        return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).build();
    }

    @Override
    public ResponseResult updateCountryCode(BindMobileReq bindMobileReq) {
        bindMobileReq.setType(13);
        String mobileKey = SmsUtil.createPhoneKey(RedisKeys.createSmsPhoneKey(bindMobileReq.getType(), bindMobileReq.getMobile()));
        String mobileCode = redisClient.get(mobileKey);
        if(!bindMobileReq.getCode().equals(mobileCode)){
            log.error("redis中的value值:"+mobileCode+"接收到的参数code"+bindMobileReq.getCode());
            throw new BizException("您输入的验证码不正确或验证码已过期!");
        }
        TUserInfo tUserInfo = new TUserInfo();
        tUserInfo.setUid(bindMobileReq.getUid());
        tUserInfo.setCountryCode(bindMobileReq.getCountryCode());
        Integer updateResult = tUserInfoMapper.updateById(tUserInfo);
        if (0 == updateResult) {
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).msg("修改手机区号失败").build();
        }
        String mobileInTheTimeKey = RedisConstant.APP_REDIS_PREFIX+"MOBILE_IN_THE_TIME";
        Integer mobileInTheTime = getAdminOptionsValue(mobileInTheTimeKey);//单位：天
        if(null != mobileInTheTime){
            String incrKey = RedisConstant.APP_REDIS_PREFIX + bindMobileReq.getMobile() + "_" + bindMobileReq.getType() + "_" + bindMobileReq.getNodeId();
            Long mobileInTheTimeSecond = mobileInTheTime * 24 * 60 * 60L;
            //计数并且，赋值失效日期
            luaScriptRedisService.doIncr(incrKey,mobileInTheTimeSecond);
        }
        return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).build();
    }

    /**
     * 设置验证码过期时间：5分钟
     * @param mobileSuffix 包含手机号码的key后缀
     * @param code 生成的验证码
     */
    private String pushCodeToRedis(String mobileSuffix, String code){
        String mobileKey = SmsUtil.createPhoneKey(mobileSuffix);
        redisClient.set(mobileKey, code);
        redisClient.expire(mobileKey, 5 * 60);
        return mobileKey;
    }

    /**
     * 获取配置项列表中的optionValue值
     * @param optionsName 配置项name
     * @return 配置项value值
     */
    private Integer getAdminOptionsValue(String optionsName){
        String getValue = redisClient.get(optionsName);
        if(null==getValue){
            String optionsValue = adminOptionsMapper.getAdminOptionValueByKey(optionsName);
            if(null == optionsValue){return null;}
            redisClient.set(optionsName,optionsValue);
            return new Integer(optionsValue);
        }
        return new Integer(getValue);
    }
}
