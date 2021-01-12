package com.xinlian.member.biz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.xinlian.biz.dao.*;
import com.xinlian.biz.model.*;
import com.xinlian.biz.utils.AdminOptionsUtil;
import com.xinlian.biz.utils.NodeVoyageUtil;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
import com.xinlian.common.enums.CheckSmsMethodEnum;
import com.xinlian.common.enums.MailTemplateEnum;
import com.xinlian.common.enums.SendRegisterTypeEnum;
import com.xinlian.common.redis.RedisKeys;
import com.xinlian.common.request.*;
import com.xinlian.common.response.*;
import com.xinlian.common.result.BizException;
import com.xinlian.common.result.ErrorInfoEnum;
import com.xinlian.common.utils.CommonUtil;
import com.xinlian.common.utils.DateFormatUtil;
import com.xinlian.common.utils.UniqueNoUtil;
import com.xinlian.member.biz.alisms.util.SmsUtil;
import com.xinlian.member.biz.chuanglan.SendSmsLogService;
import com.xinlian.member.biz.chuanglan.util.ChuangLanSmsService;
import com.xinlian.member.biz.jwt.properties.JwtPropertie;
import com.xinlian.member.biz.jwt.util.EncryptionUtil;
import com.xinlian.member.biz.jwt.util.JwtUtil;
import com.xinlian.member.biz.redis.LuaScriptRedisService;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.redis.RedisConstant;
import com.xinlian.member.biz.scheduling.LoginLogTask;
import com.xinlian.member.biz.service.IRegisterLoginService;
import com.xinlian.member.biz.service.IServerNodeService;
import com.xinlian.member.biz.service.MailService;
import com.xinlian.member.server.controller.handler.CheckSmsRuleHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RegisterLoginServiceImpl implements IRegisterLoginService {

    @Autowired
    private TUserInfoMapper userInfoMapper;
    @Autowired
    private TServerNodeMapper serverNodeMapper;
    @Autowired
    private IServerNodeService serverNodeService;
    @Autowired
    private TCurrencyInfoMapper currencyInfoMapper;
    @Autowired
    private TWalletInfoMapper walletInfoMapper;
    @Autowired
    private TCountryDicMapper countryDicMapper;
    @Autowired
    private TUserAuthMapper userAuthMapper;
    @Autowired
    private TChangePhoneMapper changePhoneMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private JwtPropertie jwtPropertie;
    @Autowired
    private LoginLogTask loginLogTask;
    @Autowired
    private MailService mailService;
    @Autowired
    private ChuangLanSmsService chuangLanSmsService;
    @Autowired
    private SendSmsLogService sendSmsLogService;
    @Autowired
    private AdminOptionsUtil adminOptionsUtil;
    @Autowired
    private LuaScriptRedisService luaScriptRedisService;
    @Autowired
    private CheckSmsRuleHandler checkSmsRuleHandler;
    @Autowired
    private NodeVoyageUtil nodeVoyageUtil;
    @Override
    public ResponseResult sendRegisterSms(RegisterReq req,boolean isInlandRegister){
        //注册 验证码  检查手机号是否可以注册
        if(isInlandRegister){ this.checkInlandCanRegister(req);}
        //需要从后台获取国家code
        //短信发送成功标识
        String phone = req.getCountryCode()==86 ? req.getPhone() : req.getCountryCode()+req.getPhone();
        String checkRegisterIsSendKey = RedisKeys.checkRegisterIsSendKey(phone, req.getType());
        Object getCheckRedisKeyValue = redisClient.get(checkRegisterIsSendKey);
        //注册短信
        if(req.getType() == 1 && null!=getCheckRedisKeyValue){
            return ResponseResult.error("短信已发送成功，注册短信30分钟内有效 !");
        }
        this.sendSmsByRegisterCode(req,checkRegisterIsSendKey);
        return ResponseResult.ok();
    }
    @Override
    public ResponseResult sendPhoneSms(NeedLoginSendSmsReq needLoginSendSmsReq){
        RegisterReq registerReq = new RegisterReq();
        registerReq.setType(needLoginSendSmsReq.getType());
        registerReq.setPhone(needLoginSendSmsReq.getPhone());
        registerReq.setCountryCode(needLoginSendSmsReq.getCountryCode());
        return this.sendRegisterSms(registerReq,false);
    }

    @Async
    public boolean sendSmsByRegisterCode(RegisterReq req,String checkRegisterIsSendKey){
        String code = EncryptionUtil.getRandomCode(6);
        //国内 短信
        String phone = req.getCountryCode()==86 ? req.getPhone() : req.getCountryCode()+req.getPhone();
        String smsRedisKey = this.pushCodeToRedis(phone,code,req.getType());
        boolean isSendFlag = false;
        if(req.getCountryCode() == 86){
            isSendFlag = chuangLanSmsService.sendRegisterCodeCh(phone, code,req,smsRedisKey);
        }else{//国外短信
            isSendFlag = chuangLanSmsService.sendRegisterCodeInte(phone, code,req,smsRedisKey);
        }
        if(isSendFlag && req.getType()==1){
            //注册场景，redis记录发送标识
            luaScriptRedisService.doIncr(checkRegisterIsSendKey,30*60L);
        }
        return isSendFlag;
    }

    private String pushCodeToRedis(String phone, String code,int reqType){
        String phoneKey = "";
        int expireTime = 5 *60;
        if(0==reqType || 1==reqType){
            reqType = 1;
            phoneKey = SmsUtil.createPhoneKey(phone,reqType);
            expireTime = 30 * 60;
        }else{
            phoneKey = SmsUtil.createPhoneKey(RedisKeys.createSmsPhoneKey(reqType,phone));
        }
        redisClient.set(phoneKey, code,expireTime);
        return phoneKey;
    }


    /**
     * 判断 手机在此节点是否可以注册
     */
    private void checkInlandCanRegister(RegisterReq req){
        //不是 注册短信
        if(req.getType() > 1 ){
            return ;
        }
        //查询节点
        TServerNode serverNode = serverNodeService.getById(req.getNodeId());
        if(serverNode == null){
            throw new BizException("不存在此节点");
        }
        //判断全局开关是否打开，开了则走全局配置，没开就走节点单独配置
        try {
            SystemSwitchRes systemSwitchRes =
                    adminOptionsUtil.fieldEntityObject(AdminOptionsBelongsSystemCodeEnum.SYSTEM_SWITCH.getBelongsSystemCode(), SystemSwitchRes.class);
            log.error("全局开关--发送注册短信001：{}", JSONObject.toJSONString(systemSwitchRes));
            if (null != systemSwitchRes && "1".equals(systemSwitchRes.getSystemApplicationFlag())) {
                log.error("全局开关--发送注册短信002");
                Integer count = userInfoMapper.selectCount(new EntityWrapper<TUserInfo>().eq("mobile", req.getPhone()));
                if (count >= Integer.parseInt(systemSwitchRes.getMobileRegisterAmount())) {
                    log.error("全局开关--发送注册短信003");
                    throw new BizException(ErrorInfoEnum.REGISTER_MOBILE_AMOUNT_OUT);
                }
            } else {
                //亚历山大 节点
                if (!canRegister(req.getPhone(), serverNode.getId())) {
                    throw new BizException("该手机号已在其他节点注册！");
                }
                //注册数量 限制 0 为 5
                int registerLimit = serverNode.getMobileRegisterAmount();
                registerLimit = registerLimit == 0 ? 5 : registerLimit;
                //已注册数量
                int count = userInfoMapper.selectCount(new EntityWrapper<TUserInfo>()
                        .eq("mobile", req.getPhone()).eq("server_node_id", req.getNodeId()));
                if (count >= registerLimit) {
                    throw new BizException("此节点此账号已注册");
                }
            }
        }catch (BizException e){
            log.error(DateFormatUtil.get(7,new Date())+"查找全局配置出现业务异常：{}",e.toString(),e);
            throw new BizException(e.getMsg());
        }catch (Exception e){
            log.error(DateFormatUtil.get(7,new Date())+"查找全局配置出现异常：{}",e.toString(),e);
            throw new BizException("获取全局配置出现异常!");
        }
        if(serverNode.getInviteStatus().intValue() == 0){
            throw new BizException("此节点不可邀请!");
        }
    }


    @Override
    public boolean checkCode(int type, String rPhone, String rCode) {
        String phoneKey = SmsUtil.createPhoneKey(RedisKeys.createSmsPhoneKey(type, rPhone));
        String code = redisClient.get(phoneKey);
        if (rCode.equals(code)) {
            return true;
        }
        checkSmsRuleHandler.doSaveSmsRuleHandler(rPhone, CheckSmsMethodEnum.SMS_QUERY_ACCOUNT_NUMBER.getMethodCode());
        return false;
    }

    @Override
    public ResponseResult<List<NodeDicRes>> findNodeDic(ServerNodeReq serverNodeReq){
        ResponseResult<List<NodeDicRes>> result = new ResponseResult<>();
        List<TServerNode> list = serverNodeMapper.selectList(new EntityWrapper<TServerNode>()
                .eq("hidden_status", 1));
        List<NodeDicRes> resList = new ArrayList<>(list.size());
        list.stream().forEach(e -> {
            resList.add(e.nodeDicRes());
        });
        //组装数据
        List<NodeDicRes> topList = new ArrayList<>();
        Map<Long, List<NodeDicRes>> map = resList.stream().collect(Collectors.groupingBy(NodeDicRes::getParentId));
        for(NodeDicRes res : resList){
            res.setChildes(map.get(res.getNodeId()));
            if(res.getParentId().longValue() == 0){
                if(serverNodeReq.getNode_type().intValue() == 0){
                    //所以节点
                    topList.add(res);
                }else if(serverNodeReq.getNode_type().intValue() == 1){
                    //除去新大陆节点
                    if(!res.getName().equals("新大陆")){
                        topList.add(res);
                    }
                }else {
                    //新大陆节点
                    if(res.getName().equals("新大陆")){
                        topList.add(res);
                    }
                }
            }
        }
        result.responseResult(GlobalConstant.ResponseCode.SUCCESS, topList);
        return result;
    }

    @Override
    public ResponseResult<List<CountryDicRes>> findCountryDic(){
        ResponseResult<List<CountryDicRes>> result = new ResponseResult<>();
        String redisKey = RedisConstant.APP_REDIS_PREFIX + RedisConstant.COUNTRY_DIC_KEY;
        List<CountryDicRes> resList = redisClient.get(redisKey);
        if(null==resList) {
            List<TCountryDic> list  = countryDicMapper.selectList(new EntityWrapper<TCountryDic>()
                    .eq("status", 1));
            resList = new ArrayList<>(list.size());
            for (int i = 0; i < list.size(); i++) {
                TCountryDic countryDic = list.get(i);
                resList.add(countryDic.countryDicRes());
            }
            redisClient.set(redisKey,resList);
        }
        result.responseResult(GlobalConstant.ResponseCode.SUCCESS, resList);
        return result;
    }

    private void getZhCountry(UserInfoRes userInfoRes){
        String redisKey = RedisConstant.APP_REDIS_PREFIX + RedisConstant.COUNTRY_SINGLE_DIC_KEY + userInfoRes.getCountryCode();
        TCountryDic countryDic = redisClient.get(redisKey);
        if(null==countryDic) {
            countryDic = countryDicMapper.getModelByCode(userInfoRes.getCountryCode());
            if(null==countryDic){return;}
            redisClient.set(redisKey,countryDic);
        }
        userInfoRes.setCountryZhName(countryDic.getZh());
        userInfoRes.setCountryEnName(countryDic.getEn());
    }


    @Override
    public ResponseResult<Integer> findHasUserName(RegisterReq req){
        ResponseResult result = new ResponseResult();
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        int count = userInfoMapper.selectCount(new EntityWrapper<TUserInfo>()
                .eq("server_node_id", req.getNodeId()).eq("user_name", req.getUsername()));
        result.setResult(count>0 ? 1 : 0);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResponseResult register(RegisterReq req,boolean isInlandRegister){
        ResponseResult result = new ResponseResult();
        result.setResult(new NullParam());
        result.setCode(GlobalConstant.ResponseCode.FAIL);
        //验证客户登录名称正则表达式
        String userNamePattern = redisClient.get(RedisConstant.REGISTER_PATTERN);
        if(null!=userNamePattern){
            boolean isMatch = Pattern.matches(userNamePattern, req.getUsername());
            if(!isMatch){
                throw new BizException("登录账号名称规则：6-16位字母加数字!");
            }
        }
        //判断同节点同用户名是否唯一
        List<TUserInfo> userInfoList =
                userInfoMapper.selectList(new EntityWrapper<TUserInfo>()
                        .eq("user_name", req.getUsername())
                        .eq("server_node_id", req.getNodeId()));
        if (null != userInfoList && 0 < userInfoList.size()) {
            result.setMsg("此用户名已存在.");
            return result;
        }
        //check 请求参数及节点注册
        TUserInfo parent = this.checkRegisterRequestParam(req);
        TServerNode serverNode = this.checkRegisterServerNode(req.getNodeId());
        //邮箱 注册 && 非大航海节点
        if(StringUtils.isBlank(req.getPhone()) && !nodeVoyageUtil.belongVoyageNode(req.getNodeId())){
            return registerMail(req,serverNode,parent);
        }
        //大航海节点
        if(nodeVoyageUtil.belongVoyageNode(req.getNodeId())){
            //注册节点是大航海节点，跨节点开关可以单向只确认注册的节点的开关
            //不可跨节点邀请
            if(serverNode.getDifferentNodeInvite() == 0
                    && parent.getServerNodeId().longValue() != req.getNodeId().longValue()){
                result.setMsg("不可跨节点邀请");
                return result;
            }
            //邮箱注册 &&
            if(!StringUtils.isBlank(req.getPhone()) && req.getPhone().contains("@")) {
                req.setEmail(req.getPhone());//app沿用一个字段
                req.setPhone(null);
                return registerMailToSeaPatrol(req, serverNode, parent);
            }
        }
        //验证 验证码
        String phone = req.getCountryCode()==86 ? req.getPhone() : req.getCountryCode()+req.getPhone();
        String redisPhoneKey = SmsUtil.createPhoneKey(phone,1);
        String redisCode = redisClient.get(redisPhoneKey);
        //所有注册验证码请求都记录下来
        sendSmsLogService.saveCheckErrorSmsCode(redisPhoneKey,redisCode,phone,req);
        if(redisCode == null || !redisCode.equals(req.getCode())){
            result.setMsg("验证码有误");
            checkSmsRuleHandler.doSaveSmsRuleHandler(phone,SendRegisterTypeEnum.REGISTER.getType()+"");
            return result;
        }
        checkSmsRuleHandler.doDeleteSmsRuleHandler(phone, SendRegisterTypeEnum.REGISTER.getType()+"");

        //注册节点不是大航海的
        if(isInlandRegister) {
            //注册节点不是大航海节点的，则需要双向确认跨节点开关是否都开启
            if (parent.getServerNodeId().longValue() != req.getNodeId().longValue()) {
                TServerNode parentNode = serverNodeService.getById(parent.getServerNodeId());
                if(null == parentNode){
                    result.setMsg("邀请码所在节点不存在");
                    return result;
                }
                if (serverNode.getDifferentNodeInvite() == 0 || parentNode.getDifferentNodeInvite() == 0) {
                    result.setMsg("注册节点或邀请码所在节点不可跨节点邀请");
                    return result;
                }
            }

            //判断全局开关是否打开，开了则走全局配置，没开就走节点单独配置
            try {
                SystemSwitchRes systemSwitchRes =
                        adminOptionsUtil.fieldEntityObject(AdminOptionsBelongsSystemCodeEnum.SYSTEM_SWITCH.getBelongsSystemCode(), SystemSwitchRes.class);
                if (null != systemSwitchRes && "1".equals(systemSwitchRes.getSystemApplicationFlag())) {
                    Integer count = userInfoMapper.selectCount(new EntityWrapper<TUserInfo>().eq("mobile", req.getPhone()));
                    if (count >= Integer.parseInt(systemSwitchRes.getMobileRegisterAmount())) {
                        result.setCode(ErrorInfoEnum.REGISTER_MOBILE_AMOUNT_OUT.getCode());
                        result.setMsg(ErrorInfoEnum.REGISTER_MOBILE_AMOUNT_OUT.getMsg());
                        return result;
                    }
                } else {
                    //亚历山大 节点
                    if (!canRegister(req.getPhone(), serverNode.getId())) {
                        result.setMsg("该手机号已在其他节点注册！");
                        return result;
                    }
                    //注册数量 限制 0 为 5
                    int registerLimit = serverNode.getMobileRegisterAmount();
                    registerLimit = registerLimit == 0 ? 5 : registerLimit;
                    //已注册数量
                    int count = userInfoMapper.selectCount(new EntityWrapper<TUserInfo>()
                            .eq("mobile", req.getPhone()).eq("server_node_id", req.getNodeId()));
                    if (count >= registerLimit) {
                        result.setMsg("此节点此账号已注册");
                        return result;
                    }
                }
            } catch (Exception e) {
                log.error("查找全局配置出现异常：{}", e.toString(), e);
            }
        }

        //添加用户
        TUserInfo info = null;
        try {
            info = addUser(req, parent, serverNode);
        }catch (BizException e){
            log.error(DateFormatUtil.getByNowTime(7)+"注册账号出现业务异常,请稍后重试:{}",e.getMessage(), e);
            result.setMsg("注册账号出现业务异常,请稍后重试!");
            return result;
        }catch(Exception e){
            log.error(DateFormatUtil.getByNowTime(7)+"注册账号出现异常,请稍后重试:{}",e.getMessage(), e);
            result.setMsg("注册账号出现异常,请稍后重试!");
            return result;
        }
        //初始化 用户钱包
        initUserWallet(info);
        //删除 验证码key
        //redisClient.deleteByKey(SmsUtil.createPhoneKey(req.getPhone()));
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        result.setResult(info.getUid());
        return result;
    }

    private TServerNode checkRegisterServerNode(@NotNull Long nodeId){
        //查询节点
        TServerNode serverNode = serverNodeService.getById(nodeId);
        if(serverNode == null){
            throw new BizException("不存在此节点");
        }
        if(serverNode.getInviteStatus() == 0){
            throw new BizException("此节点不可邀请");
        }
        return serverNode;
    }

    private TUserInfo checkRegisterRequestParam(RegisterReq req) {
        //验证密码
        if(!req.getPassword().equals(req.getPassword2())){
            throw new BizException("登录密码不一致");
        }
        if(!req.getDealPsw().equals(req.getDealPsw2())){
            throw new BizException("支付密码不一致");
        }
        if(req.getPassword().equals(req.getDealPsw())){
            throw new BizException("支付密码和登录密码不可相同");
        }
        //查询邀请人
        TUserInfo whereUserInfo = new TUserInfo();
        whereUserInfo.setInvitationCode(req.getInviteCode());
        TUserInfo getUserModel = userInfoMapper.selectOne(whereUserInfo);
        if(null==getUserModel || !getUserModel.getInvitationCode().equalsIgnoreCase(req.getInviteCode())){
            throw new BizException("邀请人不存在或者邀请码有误");
        }
        return getUserModel;
    }


    private boolean canRegister(String phone, long nodeId){
        if(nodeId != 110){
            return true;
        }
        int count = userInfoMapper.selectCount(new EntityWrapper<TUserInfo>()
                .eq("mobile", phone).ne("server_node_id", 110));
        if(count > 0){
            return false;
        }
        return true;
    }

    private TUserInfo addUser(RegisterReq req, TUserInfo parent, TServerNode serverNode){
        TUserInfo userInfo = new TUserInfo();
        //userInfo.setEmail(req.);
        userInfo.setUserName(req.getUsername());
        userInfo.setUserNameNew(req.getUsername());
        userInfo.setMobile(req.getPhone());
        userInfo.setServerNodeId(serverNode.getId());
        userInfo.setServerNodeName(serverNode.getName());
        String salt = EncryptionUtil.getSalt();
        userInfo.setPayPassWord(EncryptionUtil.md5Two(req.getDealPsw(), salt));
        userInfo.setLoginPassWord(EncryptionUtil.md5Two(req.getPassword(), salt));
        userInfo.setParentId(parent.getUid());
        userInfo.setParentName(parent.getUserName());
        //存放在redis一天 进行出重比较，存在重试10次，
        userInfo.setInvitationCode(this.getUniquenessInvitationCode());
        userInfo.setLevelStatus(1);
        userInfo.setCountryCode(req.getCountryCode());
        userInfo.setSalt(salt);
        userInfo.setCreateTime(new Date());
        userInfo.setEmail(req.getEmail());
        if (StringUtils.isNotBlank(req.getProvinceName()) && StringUtils.isNotBlank(req.getCityName())) {
            userInfo.setProvinceName(req.getProvinceName());
            userInfo.setCityName(req.getCityName());
        }
        if(StringUtils.isNotBlank(req.getPhone())){
            userInfo.setRegType(0);
        }else if(StringUtils.isNotBlank(req.getEmail())){
            userInfo.setRegType(1);
        }
        userInfoMapper.insert(userInfo);
        return userInfo;
    }
    private static ThreadLocal<Integer> tryCreateInvitationCodeNumber = new ThreadLocal<Integer>(){
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };
    /**
     * 获取唯一推荐码，重试10次
     * @return
     */
    private String getUniquenessInvitationCode(){
        String redisKey = RedisConstant.APP_REDIS_PREFIX + "USER_INVITATION_CODE";
        String invitationCode = UniqueNoUtil.createInvitationCode();
        int tryNumber = tryCreateInvitationCodeNumber.get().intValue();
        double resultIncrValue = redisClient.zincrementScore(redisKey,invitationCode,1,CommonUtil.getTheDayResidueSecond());
        if(resultIncrValue > 1 && tryNumber < 20){
            tryCreateInvitationCodeNumber.set(++tryNumber);
            return getUniquenessInvitationCode();
        }else if(resultIncrValue > 1 && tryNumber >= 20){
            throw new BizException("注册账号出现异常,请稍后重试。");
        }
        return invitationCode;
    }

	private void initUserWallet(TUserInfo userInfo) {
		List<TCurrencyInfo> list = currencyInfoMapper.selectList(new EntityWrapper<TCurrencyInfo>());
		if (list.isEmpty()) {
			return;
		}
		List<TWalletInfo> walletInfoList = new ArrayList<>(list.size());
		for (TCurrencyInfo currencyInfo : list) {
			TWalletInfo walletInfo = new TWalletInfo();
			walletInfo.setCurrencyId(currencyInfo.getId());
			walletInfo.setUid(userInfo.getUid());
			walletInfo.setBalanceNum(BigDecimal.ZERO);
			walletInfo.setCurrencyCode(currencyInfo.getCurrencyCode());
			walletInfo.setFrozenNum(BigDecimal.ZERO);
			walletInfo.setServerNodeId(userInfo.getServerNodeId());
			walletInfoList.add(walletInfo);
		}
		walletInfoMapper.insertBatch(walletInfoList);
	}

    @Override
    public ResponseResult<UserInfoRes> login(LoginReq req){
        ResponseResult<UserInfoRes> result = new ResponseResult<>();
        result.setCode(GlobalConstant.ResponseCode.FAIL);
        result.setResult(new UserInfoRes());
        List<TUserInfo> userInfoList = userInfoMapper.selectList(new EntityWrapper<TUserInfo>()
                .eq("user_name", req.getUsername()).eq("server_node_id", req.getNodeId()));
        if(userInfoList.isEmpty()){
            result.setMsg("用户不存在");
            return result;
        }
        TUserInfo info = userInfoList.get(0);
        String pwd = EncryptionUtil.md5Two(req.getPassword(), info.getSalt());
        if(!info.getLoginPassWord().equals(pwd)){
            result.setMsg("登录密码或用户名有误");
            return result;
        }
        //冻结
        if(info.getLevelStatus() == 0){
            String showFreezeReason = "  ";
            if(StringUtils.isNotEmpty(info.getShowFreezeReason())){
                showFreezeReason = "原因：".concat(info.getShowFreezeReason());
            }
            throw new BizException(ErrorInfoEnum.SHOW_FREEZE_REASON.getCode(),showFreezeReason);
        }
        //校验省市信息
        if (StringUtils.isNotBlank(info.getProvinceName()) && StringUtils.isNotBlank(info.getCityName())) {
            if (!info.getProvinceName().equals(req.getProvinceName()) || !info.getCityName().equals(req.getCityName())) {
                result.setMsg("省市信息有误");
                return result;
            }
        }
        String token = jwtPropertie.getTokenPrefix() + jwtUtil.createToken(info.getServerNodeId(), info.getUid(), req.getDeviceNumber());
        UserInfoRes res = info.userInfoRes();
        //根据国家code 获取国家中文名称及英文名称
        this.getZhCountry(res);
        //获取登录人顶级节点-给大航海节点注册绑定账号使用
        res.setTopNodeId(nodeVoyageUtil.getSeaPatrolNodeId(req.getNodeId()));
        res.setToken(token);
        res.setRealAuthStatus(userRealAuthStatus(info.getUid()));
        res.setTServerNode(queryTServerNode(req.getNodeId()));
        this.addCalEarthQrUrl(res);
        this.getUserAuthAgeLimit(res);
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        result.setResult(res);
        //设置单点登录缓存
        redisClient.set("sso_"+res.getUid(),token);
        //存用户极光id和 ios安卓类型 新jid与之前存jid不相同更新值
        if(null!=req.getJid() && null!=req.getType()
                && !"".equals(req.getJid()) && !req.getJid().equals(info.getJid())){
            TUserInfo tUserInfo = new TUserInfo();
            tUserInfo.setUid(info.getUid());
            tUserInfo.setJid(req.getJid());
            tUserInfo.setType(req.getType());
            userInfoMapper.updateById(tUserInfo);
        }
        log.info("登录TOKEN:【{}】,info.getServerNodeId:{}, info.getUid:{}, req.getDeviceNumber:{}",
                token,info.getServerNodeId(), info.getUid(), req.getDeviceNumber());
        //添加登录日志
        addLoginLog(req, info.getUid());
        return result;
    }

	private TServerNode queryTServerNode(Long nodeId) {
		TServerNode tServerNode = serverNodeService.getById(nodeId);
		if (null != tServerNode) {
			TServerNode t = new TServerNode();
			t.setId(tServerNode.getId());
			t.setName(tServerNode.getName());
			t.setNickname(tServerNode.getNickname());
			t.setActiveRequireMoney(tServerNode.getActiveRequireMoney());
			t.setLogoUrl(tServerNode.getLogoUrl());
			t.setRegisterStatus(tServerNode.getRegisterStatus());
			t.setLoginStatus(tServerNode.getLoginStatus());
			t.setBindRocketStatus(tServerNode.getBindRocketStatus());
			return t;
		}
		return null;
	}

	private int userRealAuthStatus(long uid) {
		try {
			TUserAuth tUserAuth = userAuthMapper.selectOne(new TUserAuth(uid));

			if (null != tUserAuth && null != tUserAuth.getStatus() && tUserAuth.getStatus().intValue() == 3) {
				return 1;
			}
		} catch (Exception e) {
			log.error("获取客户实名信息出现异常:{}",e.toString(),e);
		}
		return 0;
	}

	private void addLoginLog(LoginReq req, long uid) {
		try {
			TLoginLog log = new TLoginLog();
			log.setCreateTime(new Date());
			log.setLatitude(req.getLatitude());
			log.setLoginIp(req.getLoginIp());
			log.setLongitude(req.getLongitude());
			log.setUid(uid);
			log.setUsername(req.getUsername());
			loginLogTask.addLoginLog(log);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * 查找算力地球socket链接地址
	 *
	 * @param res
	 */
	private void addCalEarthQrUrl(UserInfoRes res) {
		try {
			String calEarThQrUrl = adminOptionsUtil
					.findAdminOptionOne(AdminOptionsBelongsSystemCodeEnum.APP_CAL_EARTH.getBelongsSystemCode());
			if (StringUtils.isNotBlank(calEarThQrUrl)) {
				res.setCalEarthQrUrl(calEarThQrUrl);
			}
		} catch (Exception e) {
			log.error("查找算力地球socket链接地址出现异常：{}", e.toString(), e);
		}
	}

	/**
	 * 获取实名认证年龄限制区间配置
	 *
	 * @param res
	 */
	private void getUserAuthAgeLimit(UserInfoRes res) {
		// 针对矿机已经激活，但是没有提交实名认证及实名认证被驳回的会员需要重新认证的情况。这样的会员不限制实名次数和年龄。（设置在0-100岁）
		List<Long> activeNotAuthList = userAuthMapper.findActiveNotAuthList(res.getUid());
		if (null != activeNotAuthList && activeNotAuthList.size() > 0 && activeNotAuthList.contains(res.getUid())) {
			res.setAuthMinAge(0);
			res.setAuthMaxAge(100);
		} else {
			UserAuthAgeRes userAuthAgeRes = new UserAuthAgeRes();
			try {
				userAuthAgeRes = adminOptionsUtil.fieldEntityObject(
						AdminOptionsBelongsSystemCodeEnum.APP_AUTH_AGE.getBelongsSystemCode(), UserAuthAgeRes.class);
				if (null != userAuthAgeRes) {
					res.setAuthMinAge(Integer.parseInt(userAuthAgeRes.getAuthMinAge()));
					res.setAuthMaxAge(Integer.parseInt(userAuthAgeRes.getAuthMaxAge()));
				}
			} catch (Exception e) {
				log.error("获取实名认证年龄限制区间配置出现异常：{}", e.toString(), e);
			}
		}
	}

	@Override
	public ResponseResult forgetPwd(RegisterReq req) {
		String phone = SmsUtil.getCountryCodeAndPhone(req.getPhone(), req.getCountryCode());
		// 检验是否已经验证过多次
		checkSmsRuleHandler.doCheckSmsRuleHandler(phone, SendRegisterTypeEnum.FORGET_LOGIN_PWD.getType() + "");
		ResponseResult result = new ResponseResult();
		result.setResult(new NullParam());
		result.setCode(GlobalConstant.ResponseCode.FAIL);
		// 验证密码
		if (!req.getPassword().equals(req.getPassword2())) {
			result.setMsg("两次新登录密码不一致");
			return result;
		}
		List<TUserInfo> userInfoList = userInfoMapper.selectList(new EntityWrapper<TUserInfo>()
				.eq("user_name", req.getUsername()).eq("server_node_id", req.getNodeId()));
		if (userInfoList.isEmpty()) {
			result.setMsg("用户不存在");
			return result;
		}
		TUserInfo info = userInfoList.get(0);
		/*
		 * if(!req.getPhone().equals(info.getMobile())){ result.setMsg("请使用注册时的手机号码");
		 * return result; } //验证码 手机号 String phone = info.getCountryCode()==86 ?
		 * info.getMobile() : info.getCountryCode() + info.getMobile(); String code =
		 * redisClient.get(SmsUtil.createPhoneKey(phone)); if(code == null ||
		 * !code.equals(req.getCode())){ result.setMsg("验证码有误"); return result; }
		 */
		// 验证 码检查

		String msg = checkCode(req.getPhone(), phone, req.getEmail(), req.getCode(), info);
		if (msg != null) {
			result.setMsg(msg);
			return result;
		}

		String pwd = EncryptionUtil.md5Two(req.getPassword(), info.getSalt());
		if (pwd.equals(info.getPayPassWord())) {
			result.setMsg("登录密码和支付密码不可相同");
			return result;
		}
		// 校验省市信息
		if (StringUtils.isNotBlank(info.getProvinceName()) && StringUtils.isNotBlank(info.getCityName())) {
			if (!info.getProvinceName().equals(req.getProvinceName())
					|| !info.getCityName().equals(req.getCityName())) {
				result.setMsg("省市信息有误");
				return result;
			}
		}
		// 修改密码
		TUserInfo update = new TUserInfo();
		update.setUid(info.getUid());
		update.setLoginPassWord(pwd);
		userInfoMapper.updateById(update);
		// 删除验证码key
		redisClient.deleteByKey(SmsUtil.createPhoneKey(phone));
		delCodeByForgetLoginPwd(req.getPhone(), req.getEmail(), info);

		result.setCode(GlobalConstant.ResponseCode.SUCCESS);
		return result;
	}

	private String checkCode(String mobile, String rPhone, String rEmail, String rCode, TUserInfo info) {
		if (StringUtils.isNotBlank(mobile)) {
			if (!mobile.equals(info.getMobile())) {
				return "请使用注册时的手机号码";
			}
			// 验证码 手机号
			String code = redisClient.get(SmsUtil.createPhoneKey(rPhone));
			if (code == null || !code.equals(rCode)) {
				// save check code num
				checkSmsRuleHandler.doSaveSmsRuleHandler(rPhone, SendRegisterTypeEnum.FORGET_LOGIN_PWD.getType() + "");
				return "验证码有误";
			}
			// 输入对清空
			checkSmsRuleHandler.doDeleteSmsRuleHandler(rPhone, SendRegisterTypeEnum.FORGET_LOGIN_PWD.getType() + "");
			return null;
		}
		if (!rEmail.equals(info.getEmail())) {
			return "请使用注册时的邮箱";
		}
		String emailKey = RedisConstant.EMAIL_CODE_KEY_PREFIX + rEmail + "_"
				+ MailTemplateEnum.FORGET_THE_LOGIN_PASSWORD.getCode();
		String code = redisClient.get(emailKey);
		if (code == null || !code.equals(rCode)) {
			return "验证码有误";
		}
		return null;
	}

	private void delCodeByForgetLoginPwd(String rPhone, String rEmail, TUserInfo info) {
		if (StringUtils.isNotBlank(rPhone)) {
			redisClient.deleteByKey(SmsUtil.createPhoneKey(rPhone));
			return;
		}
		String emailKey = RedisConstant.EMAIL_CODE_KEY_PREFIX + rEmail + "_"
				+ MailTemplateEnum.FORGET_THE_LOGIN_PASSWORD.getCode();
		redisClient.deleteByKey(emailKey);
	}

	@Override
	public ResponseResult updatePwd(UpdatePwdReq req) {
		ResponseResult result = new ResponseResult();
		result.setResult(new NullParam());
		result.setCode(GlobalConstant.ResponseCode.FAIL);
		if (!req.getPassword().equals(req.getPassword2())) {
			result.setMsg("登录密码不一致");
			return result;
		}
		TUserInfo userInfo = userInfoMapper.selectById(req.getUid());
		if (userInfo == null) {
			result.setMsg("用户不存在");
			return result;
		}
		String pwd = EncryptionUtil.md5Two(req.getOldPwd(), userInfo.getSalt());
		if (!pwd.equals(userInfo.getLoginPassWord())) {
			result.setMsg("原密码不正确");
			return result;
		}
		pwd = EncryptionUtil.md5Two(req.getPassword(), userInfo.getSalt());

		if (pwd.equals(userInfo.getPayPassWord())) {
			result.setMsg("登录密码和支付密码不可相同");
			return result;
		}

		TUserInfo update = new TUserInfo();
		update.setUid(userInfo.getUid());
		update.setLoginPassWord(pwd);
		userInfoMapper.updateById(update);
		result.setCode(GlobalConstant.ResponseCode.SUCCESS);
		return result;
	}

	@Override
	public ResponseResult updatePayPwd(UpdatePwdReq req) {
		ResponseResult result = new ResponseResult();
		result.setResult(new NullParam());
		result.setCode(GlobalConstant.ResponseCode.FAIL);
		if (!req.getPassword().equals(req.getPassword2())) {
			result.setMsg("登录密码不一致");
			return result;
		}
		TUserInfo userInfo = userInfoMapper.selectById(req.getUid());
		if (userInfo == null) {
			result.setMsg("用户不存在");
			return result;
		}
		String pwd = EncryptionUtil.md5Two(req.getOldPwd(), userInfo.getSalt());
		if (!pwd.equals(userInfo.getPayPassWord())) {
			result.setMsg("原密码不正确");
			return result;
		}
		pwd = EncryptionUtil.md5Two(req.getPassword(), userInfo.getSalt());

		if (pwd.equals(userInfo.getLoginPassWord())) {
			result.setMsg("支付密码和登录密码不可相同");
			return result;
		}

		TUserInfo update = new TUserInfo();
		update.setUid(userInfo.getUid());
		update.setPayPassWord(pwd);
		userInfoMapper.updateById(update);
		result.setCode(GlobalConstant.ResponseCode.SUCCESS);
		return result;
	}

	@Override
	public ResponseResult forgetPayPwd(UpdatePwdReq req) {
		ResponseResult result = new ResponseResult();
		result.setResult(new NullParam());
		result.setCode(GlobalConstant.ResponseCode.FAIL);
		if (!req.getPassword().equals(req.getPassword2())) {
			result.setMsg("支付密码不一致");
			return result;
		}
		TUserInfo userInfo = userInfoMapper.selectById(req.getUid());
		if (userInfo == null) {
			result.setMsg("用户不存在");
			return result;
		}
		// 验证码
		/*
		 * String phone = userInfo.getCountryCode()==86 ? userInfo.getMobile() :
		 * userInfo.getCountryCode() + userInfo.getMobile(); String code =
		 * redisClient.get(SmsUtil.createPhoneKey(phone)); if(code == null ||
		 * !code.equals(req.getCode())){ result.setMsg("验证码有误"); return result; }
		 */
		String msg = checkCode(req.getCode(), userInfo);
		if (msg != null) {
			result.setMsg(msg);
			return result;
		}

		String pwd = EncryptionUtil.md5Two(req.getPassword(), userInfo.getSalt());

		if (pwd.equals(userInfo.getLoginPassWord())) {
			result.setMsg("支付密码和登录密码不可相同");
			return result;
		}

		TUserInfo update = new TUserInfo();
		update.setUid(userInfo.getUid());
		update.setPayPassWord(pwd);
		userInfoMapper.updateById(update);
		// 删除验证码
		// redisClient.deleteByKey(SmsUtil.createPhoneKey(phone));
		delCodeForgetPayPwd(userInfo);

		result.setCode(GlobalConstant.ResponseCode.SUCCESS);
		return result;
	}

	private String checkCode(String rCode, TUserInfo info) {
		String code = null;
		String phone = info.getCountryCode() == 86 ? info.getMobile() : info.getCountryCode() + info.getMobile();
		checkSmsRuleHandler.doCheckSmsRuleHandler(phone, SendRegisterTypeEnum.FORGET_PAY_PWD.getType() + "");
		if (StringUtils.isNotBlank(info.getMobile())) {
			// 验证码 手机号
			code = redisClient.get(SmsUtil.createPhoneKey(phone));
		}
		if (code == null) {
			String emailKey = RedisConstant.EMAIL_CODE_KEY_PREFIX + info.getEmail() + "_"
					+ MailTemplateEnum.FORGET_THE_PAY_PASSWORD.getCode();
			code = redisClient.get(emailKey);
		}
		if (code == null || !code.equals(rCode)) {
			// if code is null
			// save check code num
			checkSmsRuleHandler.doSaveSmsRuleHandler(phone, SendRegisterTypeEnum.FORGET_PAY_PWD.getType() + "");
			return "验证码有误";
		}
		checkSmsRuleHandler.doDeleteSmsRuleHandler(phone, SendRegisterTypeEnum.FORGET_PAY_PWD.getType() + "");
		return null;
	}

	private void delCodeForgetPayPwd(TUserInfo info) {
		if (StringUtils.isNotBlank(info.getMobile())) {
			String phone = info.getCountryCode() == 86 ? info.getMobile() : info.getCountryCode() + info.getMobile();
			redisClient.deleteByKey(SmsUtil.createPhoneKey(phone));
		}
		String emailKey = RedisConstant.EMAIL_CODE_KEY_PREFIX + info.getEmail() + "_"
				+ MailTemplateEnum.FORGET_THE_PAY_PASSWORD.getCode();
		redisClient.deleteByKey(emailKey);
	}

	@Override
	public ResponseResult updateUser(UpdateUserReq req) {
		ResponseResult result = new ResponseResult();
		result.setResult(new NullParam());
		TUserInfo update = new TUserInfo();
		update.setUid(req.getUid());
		if (!StringUtils.isEmpty(req.getName())) {
			update.setNickName(req.getName());
		}
		if (!StringUtils.isEmpty(req.getAvatar())) {
			update.setHeadPortraitUrl(req.getAvatar());
		}
		if (!StringUtils.isEmpty(req.getJid())) {
			update.setJid(req.getJid());
			update.setType(req.getType());
		}
		userInfoMapper.updateById(update);
		result.setCode(GlobalConstant.ResponseCode.SUCCESS);
		return result;
	}

	@Override
	public ResponseResult<UserCodeRes> findUserCode(long uid) {
		ResponseResult<UserCodeRes> result = new ResponseResult<>();
		TUserInfo userInfo = userInfoMapper.selectById(uid);
		result.setCode(GlobalConstant.ResponseCode.SUCCESS);
		result.setResult(userInfo.userCodeRes());
		return result;
	}

	@Override
	public PageResult<List<UserInfoRes>> findUserShare(IdReq req) {
		PageResult<List<UserInfoRes>> result = new PageResult<>();
		EntityWrapper<TUserInfo> wrapper = new EntityWrapper<>();
		wrapper.eq("parent_id", req.getUid());
		result.setTotal(userInfoMapper.selectCount(wrapper));
		wrapper.last("limit " + req.pickUpOffset() + "," + req.pickUpPageSize());
		List<TUserInfo> list = userInfoMapper.selectList(wrapper);
		List<UserInfoRes> resList = new ArrayList<>(list.size());
		list.stream().forEach(e -> {
			resList.add(e.userInfoRes());
		});
		result.setResult(resList);
		result.setCurPage(req.pickUpCurPage());
		result.setPageSize(req.pickUpPageSize());
		result.setCode(GlobalConstant.ResponseCode.SUCCESS);
		result.setStatus(userInfoMapper
				.selectCount(new EntityWrapper<TUserInfo>().eq("parent_id", req.getUid()).eq("orem_state", 1))
				.toString());
		return result;
	}

	@Override
	public ResponseResult<AuthenticationRes> userAuthentication(UserAuthReq req) {
		ResponseResult<AuthenticationRes> result = new ResponseResult<>();
		result.setCode(GlobalConstant.ResponseCode.FAIL);
		result.setResult(new AuthenticationRes());
		TUserInfo userInfo = userInfoMapper.selectById(req.getUid());
		String pwd = EncryptionUtil.md5Two(req.getPassword(), userInfo.getSalt());
		if (!pwd.equals(userInfo.getPayPassWord())) {
			result.setMsg("支付密码不正确");
			return result;
		}
		AuthenticationRes res = userInfo.authenticationRes();
		// 查询 审核 信息
		TUserAuth userAuth = findOneTUserAuth(userInfo.getUid());
		if (userAuth != null) {
			res.setAuth_scsfz(userAuth.getAuthScsfz());
			res.setAuth_sfzfm(userAuth.getAuthSfzfm());
			res.setAuth_sfzzm(userAuth.getAuthSfzzm());
			res.setAuthSn(userAuth.getAuthSn());
			res.setRealName(userAuth.getRealName());
			res.setStatus(userAuth.getStatus());
		}
		result.setResult(res);
		result.setCode(GlobalConstant.ResponseCode.SUCCESS);
		return result;
	}

	private TUserAuth findOneTUserAuth(long uid) {
		List<TUserAuth> list = userAuthMapper.selectList(new EntityWrapper<TUserAuth>().eq("uid", uid));
		return list.isEmpty() ? null : list.get(0);
	}

	@Override
	public ResponseResult<Integer> hasPayPwd(long uid) {
		ResponseResult<Integer> result = new ResponseResult<Integer>();
		result.setCode(GlobalConstant.ResponseCode.SUCCESS);
		result.setResult(0);
		TUserInfo userInfo = userInfoMapper.selectById(uid);
		if (StringUtils.isEmpty(userInfo.getPayPassWord())) {
			return result;
		}
		result.setResult(1);
		return result;
	}

	@Override
	public ResponseResult changePhone(ChangePhoneReq req) {
		// System.out.println(JSON.toJSONString(req));
		ResponseResult result = new ResponseResult();
		result.setCode(GlobalConstant.ResponseCode.FAIL);
		result.setResult(new NullParam());
		TUserInfo userInfo = userInfoMapper.selectById(req.getUid());
		String phone = 86 == userInfo.getCountryCode() ? userInfo.getMobile()
				: userInfo.getCountryCode() + userInfo.getMobile();
		String code = redisClient.get(SmsUtil.createPhoneKey(phone));

        //TODO test
        /*if(code == null){
            code = "200108";
        }*/

        if(code == null || !code.equals(req.getOldCode())){
            result.setMsg("原手机号验证码有误");
            checkSmsRuleHandler.doSaveSmsRuleHandler(phone,"CHANGE_PHONE");
            return result;
        }
        String newPhone = 86 == req.getCountryCode()? req.getNewPhone():req.getCountryCode()+req.getNewPhone();
        String newCode = redisClient.get(SmsUtil.createPhoneKey(newPhone));

        //TODO test
       /* if(newCode == null){
            newCode = "200108";
        }*/


        if(newCode == null || !newCode.equals(req.getNewCode())){
            result.setMsg("新手机号验证码有误");
            checkSmsRuleHandler.doSaveSmsRuleHandler(newPhone,"CHANGE_PHONE");
            return result;
        }
        //新手机号 已经注册量
        //查询节点
        TServerNode serverNode = serverNodeService.getById(userInfo.getServerNodeId());
        //注册数量 限制 0 为 5
        int registerLimit = serverNode.getMobileRegisterAmount();
        registerLimit = registerLimit==0 ? 5 : registerLimit;
        //已注册数量
        int count = userInfoMapper.selectCount(new EntityWrapper<TUserInfo>()
                .eq("mobile", req.getNewPhone())
                .eq("server_node_id", userInfo.getServerNodeId()));
        if(count >= registerLimit){
            result.setMsg("此节点此账号已注册");
            return result;
        }
        TUserInfo update = new TUserInfo();
        update.setUid(userInfo.getUid());
        update.setCountryCode(req.getCountryCode());
        update.setMobile(req.getNewPhone());
        userInfoMapper.updateById(update);
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        return result;
    }

    @Override
    public ResponseResult changePhone(ChangePhoneIdcardReq req){
        ResponseResult result = new ResponseResult();

        String newCode = redisClient.get(SmsUtil.createPhoneKey(req.getPhone()));

        //TODO test
        /*if(newCode == null){
            newCode = "200108";
        }*/

        if(newCode == null || !newCode.equals(req.getCode())){
            result.setMsg("新手机号验证码有误");
            return result;
        }

        //查询 提交记录
        int count = changePhoneMapper.selectCount(new EntityWrapper<TChangePhone>()
                .eq("uid", req.getEuid()).eq("status", 0));
        if(count > 0){
            result.setMsg("已提交申请，待审核中");
            return result;
        }
        req.setId(null);
        req.setCreateTime(System.currentTimeMillis());
        changePhoneMapper.insert(req);
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        return  result;
    }

    @Override
    public ResponseResult<Integer> changePhoneStatus(long uid){
        ResponseResult<Integer> result = new ResponseResult<>();
        result.setResult(-1);
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        List<TChangePhone> list = changePhoneMapper.selectList(new EntityWrapper<TChangePhone>()
                .eq("uid", uid).orderBy("create_time", false));
        if(list.isEmpty()){
            return result;
        }
        result.setResult(list.get(0).getStatus());
        return result;
    }

    @Override
    public ResponseResult sendMailCode(MailSendReq req){
        ResponseResult result = new ResponseResult();
        result.setCode(GlobalConstant.ResponseCode.FAIL);
        try{
            String code = EncryptionUtil.getRandomCode(6);
            mailService.sendSimpleMailCode(req.getEmail(), "哥伦布注册验证码", "验证码：" + code);
            //写入 redis
            pushMailCodeToRedis(req.getEmail(), code);
        }catch (Exception e){
            log.error(DateFormatUtil.getByNowTime(7)+"邮件发送失败，请稍候重试:{}",e.getMessage(), e);
            result.setMsg("邮件发送失败，请稍候重试!");
            return result;
        }
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        return result;
    }

    private void pushMailCodeToRedis(String mail, String code){
        String phoneKey = RedisKeys.mailCodeKey(mail);
        redisClient.set(phoneKey, code);
        redisClient.expire(phoneKey, 5 * 60);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResponseResult registerMailToSeaPatrol(RegisterReq req,TServerNode serverNode,TUserInfo parent){
        ResponseResult result = new ResponseResult();
        result.setResult(new NullParam());
        result.setCode(GlobalConstant.ResponseCode.FAIL);
        if (!req.getEmail()
                .matches("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$")) {
            throw new BizException("请输入正确邮箱格式!");
        }
	    String emailRedisKey = RedisKeys.getSendEmailKey(RedisConstant.EMAIL_CODE_KEY_PREFIX,MailTemplateEnum.REGISTER_TYPE.getCode(),req.getEmail());
	    String emailCode = redisClient.get(emailRedisKey);
        if(!req.getCode().equals(emailCode)){
            throw new BizException("邮箱验证码过期或者错误!");
        }
        //添加用户
        TUserInfo info = null;
        try {
            info = this.addUser(req, parent, serverNode);
        }catch (BizException e){
            log.error(DateFormatUtil.printLogToGetNowTime()+"注册账号出现业务异常,请稍后重试:{}",e.getMessage(), e);
            result.setMsg("注册账号出现业务异常,请稍后重试!");
            return result;
        }catch(Exception e){
            log.error(DateFormatUtil.printLogToGetNowTime()+"邮箱注册账号出现异常，请稍候重试:{}",e.getMessage(), e);
            result.setMsg("邮箱注册账号出现异常,请稍后重试!");
            return result;
        }
        //初始化 用户钱包
        initUserWallet(info);
        //删除 验证码key
        redisClient.deleteByKey(emailRedisKey);
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        result.setResult(info.getUid());
        return result;
    }

    @Deprecated
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResponseResult registerMail(RegisterReq req,TServerNode serverNode,TUserInfo parent){
        ResponseResult result = new ResponseResult();
        result.setResult(new NullParam());
        result.setCode(GlobalConstant.ResponseCode.FAIL);
        String code = redisClient.get(RedisKeys.mailCodeKey(req.getEmail()));
        if(code == null || !code.equals(req.getCode())){
            result.setMsg("验证码有误");
            return result;
        }
        //亚历山大 节点
        if(!canRegisterMail(req.getEmail(), serverNode.getId())){
            result.setMsg("该邮箱已在其他节点注册！");
            return result;
        }
        //注册数量 限制 0 为 5
        int registerLimit = serverNode.getMobileRegisterAmount();
        registerLimit = registerLimit==0 ? 5 : registerLimit;
        //已注册数量
        int count = userInfoMapper.selectCount(new EntityWrapper<TUserInfo>()
                .eq("email", req.getEmail()).eq("server_node_id", req.getNodeId()));
        if(count >= registerLimit){
            result.setMsg("此节点此账号已注册");
            return result;
        }
        //不可跨节点邀请
        if(serverNode.getDifferentNodeInvite().intValue() == 0
                && parent.getServerNodeId().longValue() != req.getNodeId().longValue()){
            result.setMsg("不可跨节点邀请");
            return result;
        }
        //添加用户
        TUserInfo info = null;
        try {
            info = this.addUser(req, parent, serverNode);
        }catch (BizException e){
            log.error(DateFormatUtil.getByNowTime(7)+"注册账号出现业务异常,请稍后重试:{}",e.getMessage(), e);
            result.setMsg("注册账号出现业务异常,请稍后重试!");
            return result;
        }catch(Exception e){
            log.error(DateFormatUtil.getByNowTime(7)+"邮箱注册账号出现异常，请稍候重试:{}",e.getMessage(), e);
            result.setMsg("邮箱注册账号出现异常,请稍后重试!");
            return result;
        }
        //初始化 用户钱包
        initUserWallet(info);
        //删除 验证码key
        redisClient.deleteByKey(RedisKeys.mailCodeKey(req.getEmail()));
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        return result;
    }

    private boolean canRegisterMail(String email, long nodeId){
        if(nodeId != 110){
            return true;
        }
        int count = userInfoMapper.selectCount(new EntityWrapper<TUserInfo>()
                .eq("email", email).ne("server_node_id", 110));
        if(count > 0){
            return false;
        }
        return true;
    }


}