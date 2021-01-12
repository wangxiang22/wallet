package com.xinlian.member.biz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.xinlian.biz.dao.*;
import com.xinlian.biz.model.*;
import com.xinlian.biz.utils.AdminOptionsUtil;
import com.xinlian.biz.utils.NodeVoyageUtil;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
import com.xinlian.common.enums.CommonEnum;
import com.xinlian.common.enums.CurrencyEnum;
import com.xinlian.common.enums.ErrorCode;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.response.UserAuthAgeRes;
import com.xinlian.common.response.UserInfoRes;
import com.xinlian.common.result.BizException;
import com.xinlian.common.scedule.WithdrawTradeSuccessLogSave;
import com.xinlian.common.utils.UniqueNoUtil;
import com.xinlian.member.biz.jwt.util.EncryptionUtil;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.redis.RedisConstant;
import com.xinlian.member.biz.service.OremService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OremServiceImpl implements OremService {
    @Autowired
    private TUserInfoMapper tUserInfoMapper;
    @Autowired
    private TServerNodeMapper serverNodeMapper;
    @Autowired
    private TWalletInfoMapper tWalletInfoMapper;
    @Autowired
    private TLockPositionMapper lockPositionMapper;
    @Autowired
    private TUserExchangeWalletMapper tUserExchangeWalletMapper;
    @Autowired
    private TWalletTradeOrderMapper tWalletTradeOrderMapper;
    @Autowired
    private TUserAuthMapper tUserAuthMapper;
    @Autowired
    private AdminOptionsUtil adminOptionsUtil;
    @Autowired
    private WithdrawTradeSuccessLogSave withdrawTradeSuccessLogSave;
    @Autowired
    private NodeVoyageUtil nodeVoyageUtil;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private TCountryDicMapper countryDicMapper;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResponseResult activateOrem(TUserInfo tUserInfo) {
        //1验证用户支付密码
        //2扣费
        //3激活成功
        TUserInfo queryInfo = new TUserInfo();
        queryInfo.setUid(tUserInfo.getUid());
        TUserInfo queryResult = tUserInfoMapper.selectOne(queryInfo);
        String paramPassWord = tUserInfo.getPayPassWord();
        String resultPassWord = queryResult.getPayPassWord();
        //用户传入的支付密码和库里的比对
        String md5PassWord = EncryptionUtil.md5Two(paramPassWord, queryResult.getSalt());
        if (!md5PassWord.equals(resultPassWord)) {
            return ResponseResult.builder().code(ErrorCode.REQ_ERROR.getCode()).result(new JSONObject()).msg(CommonEnum.PAY_PASSWORD_NOT_MATCH.getDes()).build();
        }
        //验证成功后扣费
        Long serverNodeId = queryResult.getServerNodeId();
        TServerNode tServerNode = new TServerNode();
        tServerNode.setId(serverNodeId);
        //根据用户所属节点查询节点信息
        TServerNode nodeResult = serverNodeMapper.selectOne(tServerNode);
        //如果该节点不可激活
        if (nodeResult.getActiveStatus() == 0) {
            return ResponseResult.builder().code(ErrorCode.REQ_ERROR.getCode()).result(new JSONObject()).msg(CommonEnum.NODE_NOT_OPEN.getDes()).build();
        }
        TUserInfo userInfo = new TUserInfo();
        //设置已激活
        userInfo.setOremState(1);
        EntityWrapper<TUserInfo> wrapper = new EntityWrapper<>();
        wrapper.eq("uid", tUserInfo.getUid());
        wrapper.eq("orem_state", 0);
        int count = tUserInfoMapper.update(userInfo, wrapper);
        //如果没有更新 说明已经实名了
        if (count == 0) {
            return ResponseResult.builder().code(ErrorCode.REQ_ERROR.getCode()).result(new JSONObject()).msg(CommonEnum.ALREADY_ACTIVATE.getDes()).build();
        }
        //获取激活矿机所需usdt
        BigDecimal activeRequireMoney = nodeResult.getActiveRequireMoney();
        //从用户钱包减少相应usdt
        TWalletInfo tWalletInfo = new TWalletInfo();
        tWalletInfo.setUid(tUserInfo.getUid());
        //设置扣费币种为usdt
        tWalletInfo.setCurrencyId(Long.parseLong(CurrencyEnum.USDT.getCurrencyId() + ""));
        tWalletInfo.setBalanceNum(activeRequireMoney);
        //查询用户是否已激活
        Integer oremState = queryResult.getOremState();
        if (oremState == 1) {
            return ResponseResult.builder().code(ErrorCode.REQ_ERROR.getCode()).result(new JSONObject()).msg(CommonEnum.ALREADY_ACTIVATE.getDes()).build();
        }
        //进行扣费
        int i = tWalletInfoMapper.substactMoneyForOrem(tWalletInfo);
        //扣费失败
        if (i == 0) {
            throw new BizException("余额不足");
        }
        //扣费成功
        queryInfo.setActiveTime(new Date());
        tUserInfoMapper.updateById(queryInfo);
        TWalletTradeOrder tWalletTradeOrder = new TWalletTradeOrder();
        tWalletTradeOrder.setUid(tUserInfo.getUid());
        tWalletTradeOrder.setCurrencyId(5L);
        tWalletTradeOrder.setCurrencyCode("USDT");
        tWalletTradeOrder.setTradeCurrencyNum(activeRequireMoney.multiply(new BigDecimal(-1)));
        tWalletTradeOrder.setTradeStatus(7);
        tWalletTradeOrder.setTradeType(3);
        tWalletTradeOrder.setDes("激活矿机");
        tWalletTradeOrder.setCreateTime(new Date());
        tWalletTradeOrderMapper.insert(tWalletTradeOrder);
        //获取新大陆及其子节点的id列表
        TServerNode node = serverNodeMapper.selectById(7);
        Long[] nodeIds = (Long[]) ConvertUtils.convert(node.getChildIds().split(","),Long[].class);
        List<Long> childIds = Arrays.stream(nodeIds).collect(Collectors.toList());
        //判断用户是否为新大陆及其子节点用户
        for (Long nodeId : childIds) {
            if (serverNodeId.equals(nodeId)) {
                TLockPosition tLockPosition = new TLockPosition();
                tLockPosition.setUid(tUserInfo.getUid());
                tLockPosition.setNodeId(serverNodeId);
                tLockPosition.setUsdtAmount(activeRequireMoney.divide(new BigDecimal(2),4, BigDecimal.ROUND_DOWN));
                Integer insert = lockPositionMapper.insert(tLockPosition);
                if (0 == insert) {//uid唯一
                    return ResponseResult.builder().code(GlobalConstant.ResponseCode.PARAM_ERROR).build();
                }
            }
        }
        //账户大额变动流水记录
        withdrawTradeSuccessLogSave.addWithdrawTradeSuccessLog(tUserInfo.getUid(),Long.parseLong(CurrencyEnum.USDT.getCurrencyId() + ""),CurrencyEnum.USDT.getCurrencyCode(),
                null,null,activeRequireMoney.multiply(new BigDecimal("-1")),"激活矿机",null,new Date(), UniqueNoUtil.uuid());
        return ResponseResult.builder().code(ErrorCode.REQ_SUCCESS.getCode()).result(new JSONObject()).msg(CommonEnum.ACTIVATE_OK.getDes()).build();
    }

    @Override
    public ResponseResult isOremActivate(TUserInfo tUserInfo) {
        TUserInfo getUserInfoByDB = tUserInfoMapper.selectOne(tUserInfo);
        if (null==getUserInfoByDB.getBindCount()){
            getUserInfoByDB.setBindCount(0);
        }
        UserInfoRes userInfoRes = getUserInfoByDB.userInfoRes();
        TUserExchangeWallet tUserExchangeWallet = new TUserExchangeWallet();
        tUserExchangeWallet.setCatUid(tUserInfo.getUid());
        List<TUserExchangeWallet> tUserExchangeWallets =
                tUserExchangeWalletMapper.selectPage(new Page<TUserExchangeWallet>(1, 1)
                        ,new EntityWrapper<TUserExchangeWallet>().eq("cat_uid",tUserInfo.getUid()).orderBy("id",false));
        TUserExchangeWallet res=null;
        if (!tUserExchangeWallets.isEmpty()) {
             res= tUserExchangeWallets.get(0);
        }
        if (res!=null){
            Long rocketUid = res.getRocketUid();
            userInfoRes.setExchangeId(rocketUid);
        }
        TUserAuth tUserAuth = new TUserAuth();
        tUserAuth.setUid(getUserInfoByDB.getUid());
        TUserAuth authResult = tUserAuthMapper.selectOne(tUserAuth);
        if(null == authResult){ userInfoRes.setRealAuthStatus(0);}
        else{ userInfoRes.setRealAuthStatus(authResult.getStatus()==3?1:0);}
        //针对矿机已经激活，但是没有提交实名认证及实名认证被驳回的会员需要重新认证的情况。这样的会员不限制实名次数和年龄。（设置在0-100岁）
        List<Long> activeNotAuthList = tUserAuthMapper.findActiveNotAuthList(tUserInfo.getUid());
        if (null != activeNotAuthList && activeNotAuthList.size() > 0 && activeNotAuthList.contains(tUserInfo.getUid())) {
            userInfoRes.setAuthMinAge(0);
            userInfoRes.setAuthMaxAge(100);
            return ResponseResult.builder().code(ErrorCode.REQ_SUCCESS.getCode()).result(userInfoRes).msg(ErrorCode.REQ_SUCCESS.getDes()).build();
        }
        //根据国家code 获取国家中文名称及英文名称
        this.getZhCountry(userInfoRes);
        //获取登录人顶级节点-给大航海节点注册绑定账号使用
        userInfoRes.setTopNodeId(nodeVoyageUtil.getSeaPatrolNodeId(getUserInfoByDB.getServerNodeId()));
        //获取实名认证年龄限制区间配置
        UserAuthAgeRes userAuthAgeRes = new UserAuthAgeRes();
        try {
            userAuthAgeRes = adminOptionsUtil.fieldEntityObject(AdminOptionsBelongsSystemCodeEnum.APP_AUTH_AGE.getBelongsSystemCode(), UserAuthAgeRes.class);
            if(null != userAuthAgeRes) {
                userInfoRes.setAuthMinAge(Integer.parseInt(userAuthAgeRes.getAuthMinAge()));
                userInfoRes.setAuthMaxAge(Integer.parseInt(userAuthAgeRes.getAuthMaxAge()));
            }
        }catch (Exception e){
            log.error("获取实名认证年龄限制区间配置出现异常：{}",e.toString(),e);
        }
        return ResponseResult.builder().code(ErrorCode.REQ_SUCCESS.getCode()).result(userInfoRes).msg(ErrorCode.REQ_SUCCESS.getDes()).build();
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
}
