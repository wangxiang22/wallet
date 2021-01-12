package com.xinlian.member.biz.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.xinlian.biz.dao.TChainOwnerMapper;
import com.xinlian.biz.dao.TCurrencyManageMapper;
import com.xinlian.biz.dao.TUserAuthMapper;
import com.xinlian.biz.dao.TUserInfoMapper;
import com.xinlian.biz.model.TChainOwner;
import com.xinlian.biz.model.TCurrencyManage;
import com.xinlian.biz.model.TUserAuth;
import com.xinlian.biz.model.TUserInfo;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.request.ChainOwnerReq;
import com.xinlian.common.response.ChainOwnerAssetRes;
import com.xinlian.common.response.ChainOwnerUserRes;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.service.IChainOwnerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ChainOwnerServiceImpl implements IChainOwnerService {

    @Autowired
    private TUserInfoMapper userInfoMapper;

    @Autowired
    private TUserAuthMapper userAuthMapper;

    @Autowired
    private TChainOwnerMapper chainOwnerMapper;

    @Autowired
    private TCurrencyManageMapper currencyManageMapper;

    @Override
    public ResponseResult findChainOwnerUser(Long uid) {
        //根据uid获取用户信息
        TUserInfo userInfo = userInfoMapper.selectById(uid);
        if (null == userInfo) {
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.PARAM_ERROR).msg("用户不存在").build();
        }
        //根据uid获取用户实名信息
        TUserAuth tUserAuth = new TUserAuth();
        tUserAuth.setUid(uid);
        TUserAuth userAuth = userAuthMapper.selectOne(tUserAuth);
        if (null == userAuth) {
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.PARAM_ERROR).msg("用户未实名").build();
        }
        //两表数据组合后返回
        ChainOwnerUserRes chainOwnerUserRes = ChainOwnerUserRes
                .builder().uid(uid).email(userInfo.getEmail()).username(userInfo.getUserName()).phone(userInfo.getMobile())
                .auth_name(userAuth.getRealName()).auth_sn(userAuth.getAuthSn()).build();
        return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).result(chainOwnerUserRes).build();
    }

    @Override
    public ResponseResult updateChainOwnerUser(ChainOwnerReq chainOwnerReq) {
        //更新链权人信息（实名姓名、实名证件号及邮箱（选填））
        TChainOwner tChainOwner = new TChainOwner();
        if (StringUtils.isNotEmpty(chainOwnerReq.getAuth_name())) {
            tChainOwner.setAuthName(chainOwnerReq.getAuth_name());
        }
        if (StringUtils.isNotEmpty(chainOwnerReq.getAuth_sn())) {
            tChainOwner.setAuthSn(chainOwnerReq.getAuth_sn());
        }
        if (StringUtils.isNotEmpty(chainOwnerReq.getEmail())) {
            tChainOwner.setEmail(chainOwnerReq.getEmail());
        }
        if (StringUtils.isNotEmpty(chainOwnerReq.getUrl())) {
            tChainOwner.setUrl(chainOwnerReq.getUrl());//保存链权人证书url
            tChainOwner.setSignStatus(1);//将链权人签约状态改为已签约
        }
        Integer updateResult = chainOwnerMapper.update(tChainOwner, new EntityWrapper<TChainOwner>().eq("uid",chainOwnerReq.getUid()));
        if (0 == updateResult) {
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.PARAM_ERROR).msg("链权人信息有误").build();
        }
        return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).build();
    }

    @Override
    public ResponseResult findChainOwnerAsset(Long uid) {
        TChainOwner tChainOwner = new TChainOwner();
        tChainOwner.setUid(uid);
        TChainOwner chainOwner = chainOwnerMapper.selectOne(tChainOwner);
        //根据uid到链权人表查找信息，作为判断是否为链权人的依据
        if (null == chainOwner) {
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).msg("用户非链权人").build();
        }
        ChainOwnerAssetRes chainOwnerAssetRes = new ChainOwnerAssetRes();
        chainOwnerAssetRes.setUrl(chainOwner.getUrl());
        chainOwnerAssetRes.setSignStatus(chainOwner.getSignStatus());
        chainOwnerAssetRes.setCatAmount(chainOwner.getCatAmount());
        //从币种管理中获取CAT实时市价
        TCurrencyManage tCurrencyManage = new TCurrencyManage();
        tCurrencyManage.setCurrencyId(6L);
        BigDecimal dollar = currencyManageMapper.selectOne(tCurrencyManage).getDollar();
        BigDecimal bigDecimal = dollar.multiply(chainOwner.getCatAmount()).setScale(4, BigDecimal.ROUND_DOWN);
        chainOwnerAssetRes.setCatValue(bigDecimal);
        return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).result(chainOwnerAssetRes).build();
    }
}
