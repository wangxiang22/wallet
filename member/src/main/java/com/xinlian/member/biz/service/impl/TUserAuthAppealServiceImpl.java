package com.xinlian.member.biz.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.xinlian.biz.dao.*;
import com.xinlian.biz.model.*;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.request.UserAuthAppealReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.service.TUserAuthAppealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
public class TUserAuthAppealServiceImpl implements TUserAuthAppealService {

    @Autowired
    private TUserAuthAppealMapper userAuthAppealMapper;
    @Autowired
    private TUserInfoMapper userInfoMapper;
    @Autowired
    private TUserAuthMapper userAuthMapper;
    @Autowired
    private TServerNodeMapper serverNodeMapper;
    @Autowired
    private TChainOwnerMapper chainOwnerMapper;

    @Override
    public ResponseResult findAppealStatus(Long uid) {
        TUserAuthAppeal tUserAuthAppeal = new TUserAuthAppeal();
        tUserAuthAppeal.setUid(uid);
        TUserAuthAppeal userAuthAppeal = userAuthAppealMapper.selectOne(tUserAuthAppeal);
        if (null != userAuthAppeal) {
            //申诉中，则不会有申诉按钮，不需要判断
            //申诉通过的则不允许申诉
            if (3 == userAuthAppeal.getAppealStatus()) {
                return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).msg("当前账户的实名申诉已通过，无需再次申请，如有问题请联系客服！").build();
            }
            //申诉驳回的则再判断申诉次数是否有达上限，申诉次数达上限（商定的上限次数是两次），返回拒绝理由
            if (2 == userAuthAppeal.getAppealStatus() && 2 <= userAuthAppeal.getAppealCount()) {
                return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).msg("申诉已达上限！本次被拒理由是：" + userAuthAppeal.getNote() + "。如有问题请联系客服！").build();
            }
            //返回拒绝理由
            if (2 == userAuthAppeal.getAppealStatus() && 2 > userAuthAppeal.getAppealCount()) {
                return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).result("您上次申诉的被拒理由是：" + userAuthAppeal.getNote()).build();
            }
        }
        return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).result("为了您的账户安全，实名修改仅有2次申诉机会，请谨慎操作！").build();
    }

    @Override
    @Transactional
    public ResponseResult insertAppeal(UserAuthAppealReq userAuthAppealReq) {
        //判断是否为链权人，是的话不允许从该通道提交申诉
        TChainOwner tChainOwner = new TChainOwner();
        tChainOwner.setUid(userAuthAppealReq.getUid());
        TChainOwner chainOwner = chainOwnerMapper.selectOne(tChainOwner);
        if (null != chainOwner) {
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).msg("尊贵的链权人，为了您的账号信息安全，如发现姓名有误请联系官方人工客服处理！").build();
        }
        TUserAuth tUserAuth = new TUserAuth();
        tUserAuth.setUid(userAuthAppealReq.getUid());
        TUserAuth userAuth = userAuthMapper.selectOne(tUserAuth);//获取用户的实名信息
        TUserInfo userInfo = userInfoMapper.selectById(userAuthAppealReq.getUid());//获取用户的基本信息
        if (null == userInfo || null == userAuth) {
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).msg("用户信息有误").build();
        }
        //判断用户申诉提交的姓名是否与实名表中的真实姓名重复，重复的话不允许申诉
        if (userAuth.getRealName().equals(userAuthAppealReq.getAppealRealName())) {
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).msg("检测到您填写的姓名与原姓名一致，无需修改！").build();
        }
        TServerNode serverNode = serverNodeMapper.selectById(userAuthAppealReq.getNodeId());//获取用户所在节点的信息
        //判断提交申诉的用户是否已有提交记录，有的话覆盖写入，没有的话插入写入
        TUserAuthAppeal authAppeal = new TUserAuthAppeal();
        authAppeal.setUid(userAuthAppealReq.getUid());
        TUserAuthAppeal appeal = userAuthAppealMapper.selectOne(authAppeal);
        TUserAuthAppeal userAuthAppeal = new TUserAuthAppeal();
        userAuthAppeal.setUserName(userInfo.getUserName());
        userAuthAppeal.setNodeId(userAuthAppealReq.getNodeId());
        userAuthAppeal.setNodeName(serverNode.getName());
        userAuthAppeal.setOriginalRealName(userAuth.getRealName());
        userAuthAppeal.setAppealRealName(userAuthAppealReq.getAppealRealName());
        userAuthAppeal.setAuthSfzfm(userAuth.getAuthSfzfm());
        userAuthAppeal.setAuthSfzzm(userAuth.getAuthSfzzm());
        userAuthAppeal.setAuthScsfz(userAuth.getAuthScsfz());
        userAuthAppeal.setAppealStatus(1);
        if (null == appeal) {
            userAuthAppeal.setUid(userAuthAppealReq.getUid());
            userAuthAppeal.setAppealCount(1);//第一次提交申诉，默认次数为1
            Integer insertResult = userAuthAppealMapper.insert(userAuthAppeal);
            if (0 == insertResult) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).msg("实名申诉失败，请联系官方人工客服处理!").build();
            }
        }else {
            userAuthAppeal.setAppealCount(appeal.getAppealCount() + 1);//提交申诉次数加一
            Integer updateResult = userAuthAppealMapper.update(userAuthAppeal, new EntityWrapper<TUserAuthAppeal>().eq("uid", userAuthAppealReq.getUid()));
            if (0 == updateResult) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).msg("实名申诉失败，请联系官方人工客服处理!").build();
            }
        }
        //申诉写入成功，则修改该用户的实名信息状态为审核中
        TUserAuth auth = new TUserAuth();
        auth.setStatus(1);
        Integer updateResult = userAuthMapper.update(auth, new EntityWrapper<TUserAuth>().eq("uid", userAuthAppealReq.getUid()));
        if (0 == updateResult) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).msg("实名申诉失败，请联系官方人工客服处理!").build();
        }
        return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).build();
    }

    @Override
    public boolean queryAuthStatusByUid(Long uid) {
        return 0==userAuthMapper.queryAuthStatusByUid(uid);
    }


}
