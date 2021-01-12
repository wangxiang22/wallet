package com.xinlian.member.biz.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.xinlian.biz.dao.*;
import com.xinlian.biz.model.*;
import com.xinlian.common.enums.CommonEnum;
import com.xinlian.common.enums.ErrorCode;
import com.xinlian.common.enums.MailTemplateEnum;
import com.xinlian.common.request.DelBindReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import com.xinlian.member.biz.alisms.util.SmsUtil;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.redis.RedisConstant;
import com.xinlian.member.biz.service.TUserExchangeWalletService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wjf
 * @since 2019-12-28
 */
@Service
public class TUserExchangeWalletServiceImpl extends ServiceImpl<TUserExchangeWalletMapper, TUserExchangeWallet> implements TUserExchangeWalletService {
    @Autowired
    private TUserExchangeWalletMapper tUserExchangeWalletMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private TServerNodeMapper tServerNodeMapper;
    @Autowired
    private TUserInfoMapper tUserInfoMapper;
    @Autowired
    private TWalletInfoMapper tWalletInfoMapper;
    @Autowired
    private TWalletTradeOrderMapper tWalletTradeOrderMapper;
    @Autowired
    private TRocketBindMapper tRocketBindMapper;

    @Override
    public ResponseResult queryBindState(Long uid) {
        TUserExchangeWallet tUserExchangeWallet = new TUserExchangeWallet();
        tUserExchangeWallet.setCatUid(uid);
//        TUserExchangeWallet res = tUserExchangeWalletMapper.selectOne(tUserExchangeWallet);
//       tUserExchangeWalletMapper.selectList(new EntityWrapper<TUserExchangeWallet>().eq("cat_uid",uid).last("1"));
        List<TUserExchangeWallet> tUserExchangeWallets =
                tUserExchangeWalletMapper.selectPage(new Page<TUserExchangeWallet>(1, 1)
                        ,new EntityWrapper<TUserExchangeWallet>().eq("cat_uid",uid).orderBy("id",false));
        if (tUserExchangeWallets.isEmpty()){
            return ResponseResult.builder().msg(ErrorCode.REQ_SUCCESS.getDes()).code(ErrorCode.REQ_SUCCESS.getCode()).result(null).build();
        }
        TUserExchangeWallet res=tUserExchangeWallets.get(0);
        return ResponseResult.builder().msg(ErrorCode.REQ_SUCCESS.getDes()).code(ErrorCode.REQ_SUCCESS.getCode()).result(res).build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult BindExchange(TUserExchangeWallet tUserExchangeWallet) {
        //1.查询用户绑定次数 2.如果大于1次需要扣费检查钱包是否有钱扣 3.增加1次用户绑定次数
        queryUserBindCount(tUserExchangeWallet);
        //验证有无绑定资格
        TServerNode tServerNode = new TServerNode();
        tServerNode.setId(tUserExchangeWallet.getNode());
        TServerNode nodeResult = tServerNodeMapper.selectOne(tServerNode);
        if (nodeResult.getBindRocketStatus()==0){
            throw new BizException(CommonEnum.CAN_NOT_BIND_EXCHANGE_BY_NODE.getDes());
        }

        //先校验验证码
        String rocketPhone = tUserExchangeWallet.getRocketPhone();
        String phoneCode="";
        if (!StringUtils.isEmpty(tUserExchangeWallet.getEmail())){
            String emailKey = RedisConstant.EMAIL_CODE_KEY_PREFIX + tUserExchangeWallet.getEmail() + "_" + MailTemplateEnum.BIND_EXCHANGE_TYPE.getCode();
            phoneCode=redisClient.get(emailKey);
        }else {
            phoneCode = redisClient.get(SmsUtil.createPhoneKey(rocketPhone) + "BindEx");
        }
        if (StringUtils.isEmpty(phoneCode)){
            throw new BizException(CommonEnum.CODE_OUT_TIME.getDes());
        }
        if (!phoneCode.equals(tUserExchangeWallet.getCode())){
            throw new BizException(CommonEnum.CODE_ERROR.getDes());
        }

        TUserExchangeWallet t = tUserExchangeWalletMapper.selectOne(tUserExchangeWallet);
        //绑定记录对象构建
//        TRocketBind tRocketBind = new TRocketBind();
//        tRocketBind.setCatUid(tUserExchangeWallet.getCatUid());
//        tRocketBind.setRocketUid(tUserExchangeWallet.getRocketUid());
//        tRocketBind.setCreateTime(new Date());
//        tRocketBind.setState(1);
        if (t!=null){
            Integer status = t.getStatus();
            if (status==1){
                throw new BizException("已经绑定过啦，请勿重复绑定");
            }else {
                t.setStatus(1);
                tUserExchangeWalletMapper.updateById(t);
                tUserExchangeWallet.setCreateTime(new Date());
                TRocketBind tRocketBind = insertRocketBind(t,1);
                tRocketBindMapper.insert(tRocketBind);
                return ResponseResult.builder().msg("绑定成功").code(ErrorCode.REQ_SUCCESS.getCode()).result(null).build();
            }
        }
        //设置绑定成功
        tUserExchangeWallet.setStatus(1);
        tUserExchangeWallet.setCreateTime(new Date());
        Integer insert = tUserExchangeWalletMapper.insert(tUserExchangeWallet);

        if (insert==1) {
            TRocketBind tRocketBind = insertRocketBind(tUserExchangeWallet,1);
            tRocketBindMapper.insert(tRocketBind);
            return ResponseResult.builder().msg("绑定成功").code(ErrorCode.REQ_SUCCESS.getCode()).result(null).build();
        }else {
            throw new BizException("绑定失败");
        }
    }

    private TRocketBind insertRocketBind(TUserExchangeWallet tUserExchangeWallet,Integer type){
        TRocketBind tRocketBind = new TRocketBind();
        BeanUtils.copyProperties(tUserExchangeWallet,tRocketBind);
        tRocketBind.setId(null);
        tRocketBind.setCreateTime(new Date());
        tRocketBind.setState(type);
        return tRocketBind;
    }

    private void queryUserBindCount(TUserExchangeWallet tUserExchangeWallet) {
        //1.查询用户绑定次数 2.如果大于1次需要扣费检查钱包是否有钱扣 3.增加1次用户绑定次数
        TUserInfo tUserInfo =new TUserInfo();
        tUserInfo.setUid(tUserExchangeWallet.getCatUid());
        TUserInfo userInfoResult = tUserInfoMapper.selectOne(tUserInfo);
        if (null!=userInfoResult.getBindCount()&&userInfoResult.getBindCount()>0){
            //绑定超过1次需要扣费了
            TWalletInfo tWalletInfo = new TWalletInfo();
            tWalletInfo.setUid(tUserExchangeWallet.getCatUid());
            tWalletInfo.setCurrencyId(5L);
            TWalletInfo walletInfoResult = tWalletInfoMapper.selectOne(tWalletInfo);
            if (walletInfoResult.getBalanceNum().compareTo(new BigDecimal(1))>=0){
                tWalletInfo.setBalanceNum(walletInfoResult.getBalanceNum().subtract(new BigDecimal(1)));
                tWalletInfoMapper.update(tWalletInfo,new EntityWrapper<TWalletInfo>()
                        .eq("uid",tUserExchangeWallet.getCatUid())
                        .eq("currency_id",5L));
                tUserInfo.setBindCount(userInfoResult.getBindCount()+1);
                tUserInfoMapper.update(tUserInfo,new EntityWrapper<TUserInfo>().eq("uid",tUserExchangeWallet.getCatUid()));
                TWalletTradeOrder tWalletTradeOrder = new TWalletTradeOrder();
                tWalletTradeOrder.setUid(tUserExchangeWallet.getCatUid());
                tWalletTradeOrder.setDes("绑定交易所扣费");
                tWalletTradeOrder.setTradeCurrencyNum(new BigDecimal(-1));
                tWalletTradeOrder.setCreateTime(new Date());
                tWalletTradeOrder.setCurrencyId(5L);
                tWalletTradeOrder.setCurrencyCode("USDT");
                tWalletTradeOrder.setTradeType(3);
                tWalletTradeOrder.setTradeStatus(7);
                tWalletTradeOrderMapper.insert(tWalletTradeOrder);
                return;
            }else {
                throw new BizException("余额不足");
            }
        }
        if (null==userInfoResult.getBindCount()||0==userInfoResult.getBindCount()){
            tUserInfo.setBindCount(1);
            tUserInfoMapper.update(tUserInfo,new EntityWrapper<TUserInfo>().eq("uid",tUserExchangeWallet.getCatUid()));
        }
    }

    @Override
    public ResponseResult  delBindReq(DelBindReq delBindReq) {
        if (!StringUtils.isEmpty(delBindReq.getCode())){
            String realCode = "";
            if (!StringUtils.isEmpty(delBindReq.getEmail())){
                realCode =redisClient.get(RedisConstant.EMAIL_CODE_KEY_PREFIX + delBindReq.getEmail() + "_" + MailTemplateEnum.CANCEL_BIND_EXCHANGE_TYPE.getCode());
            }else {
                realCode = redisClient.get(SmsUtil.createPhoneKey(delBindReq.getPhone()) + "delBind");
            }
            if (StringUtils.isEmpty(realCode)){
                ResponseResult.builder().msg(CommonEnum.CODE_OUT_TIME.getDes()).code(ErrorCode.REQ_ERROR.getCode()).result(null).build();
            }
            if (realCode.equals(delBindReq.getCode())){
                TUserExchangeWallet tUserExchangeWallet = new TUserExchangeWallet();
                tUserExchangeWallet.setCatUid(delBindReq.getUid());
                TUserExchangeWallet result = tUserExchangeWalletMapper.selectOne(tUserExchangeWallet);
                tUserExchangeWalletMapper.delete(new EntityWrapper<TUserExchangeWallet>().eq("cat_uid",delBindReq.getUid()));
                TRocketBind tRocketBind = insertRocketBind(result, 2);
                tRocketBindMapper.insert(tRocketBind);
                return ResponseResult.builder().msg(CommonEnum.DEL_BIND_OK.getDes()).code(ErrorCode.REQ_SUCCESS.getCode()).result(null).build();
            }else {
                return ResponseResult.builder().msg(CommonEnum.CODE_ERROR.getDes()).code(ErrorCode.REQ_ERROR.getCode()).result(null).build();
            }
        }
        return ResponseResult.builder().msg(CommonEnum.CODE_NULL.getDes()).code(ErrorCode.REQ_ERROR.getCode()).result(null).build();
    }
}
