package com.xinlian.admin.biz.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.xinlian.admin.biz.service.UserAuthAppealManagerService;
import com.xinlian.biz.dao.TUserAuthAppealMapper;
import com.xinlian.biz.dao.TUserAuthMapper;
import com.xinlian.biz.model.TUserAuth;
import com.xinlian.biz.model.TUserAuthAppeal;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.request.UserAuthAppealManagerReq;
import com.xinlian.common.request.UserAuthAppealSubmitReq;
import com.xinlian.common.response.PageResult;
import com.xinlian.common.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;

/**
 * @author lt
 */
@Service
public class UserAuthAppealManagerServiceImpl implements UserAuthAppealManagerService {
    @Autowired
    private TUserAuthAppealMapper userAuthAppealMapper;
    @Autowired
    private TUserAuthMapper userAuthMapper;

    @Override
    public PageResult<List<TUserAuthAppeal>> queryAppealListPage(UserAuthAppealManagerReq userAuthAppealManagerReq) {
        PageResult<List<TUserAuthAppeal>> result = new PageResult<>();
        result.setCurPage(userAuthAppealManagerReq.pickUpCurPage());
        result.setPageSize(userAuthAppealManagerReq.pickUpPageSize());
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        EntityWrapper<TUserAuthAppeal> wrapper = new EntityWrapper<>();
        if (null != userAuthAppealManagerReq.getUid() && !"".equals(String.valueOf(userAuthAppealManagerReq.getUid()))) {
            wrapper.eq("uid",userAuthAppealManagerReq.getUid());
        }
        if (null != userAuthAppealManagerReq.getAppealStatus() && !"".equals(String.valueOf(userAuthAppealManagerReq.getAppealStatus()))) {
            wrapper.eq("appeal_status",userAuthAppealManagerReq.getAppealStatus());
        }
        result.setTotal(userAuthAppealMapper.selectCount(wrapper));
        result.setResult(userAuthAppealMapper.selectPage(new Page<TUserAuthAppeal>((int) userAuthAppealManagerReq.pickUpCurPage(),
                        (int) userAuthAppealManagerReq.pickUpPageSize()), wrapper.orderBy("update_time",false)));
        return result;
    }

    @Override
    @Transactional
    public ResponseResult updateAppealStatus(UserAuthAppealSubmitReq userAuthAppealSubmitReq) {
        TUserAuthAppeal tUserAuthAppeal = new TUserAuthAppeal();
        tUserAuthAppeal.setUid(userAuthAppealSubmitReq.getUid());
        TUserAuthAppeal userAuthAppeal = userAuthAppealMapper.selectOne(tUserAuthAppeal);
        TUserAuth tUserAuth = new TUserAuth();
        tUserAuth.setUid(userAuthAppealSubmitReq.getUid());
        TUserAuth userAuth = userAuthMapper.selectOne(tUserAuth);
        if (null == userAuthAppeal || null == userAuth) {
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).msg("用户uid有误").build();
        }
        //申诉审核状态 - 1：待审核，2：已拒绝，3：已更正
        TUserAuthAppeal authAppeal = new TUserAuthAppeal();
        authAppeal.setAppealStatus(userAuthAppealSubmitReq.getAppealStatus());
        TUserAuth auth = new TUserAuth();
        auth.setStatus(3);
        if (2 == userAuthAppealSubmitReq.getAppealStatus()) {
            //申诉审核拒绝，申诉状态修改，实名认证表审核状态修改回原来的审核通过
            authAppeal.setNote(userAuthAppealSubmitReq.getNote());
            Integer updateAppealRes = userAuthAppealMapper.update(authAppeal, new EntityWrapper<TUserAuthAppeal>().eq("uid", userAuthAppealSubmitReq.getUid()));
            if (0 == updateAppealRes) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).msg("审核操作有误").build();
            }
            Integer updateAuthRes = userAuthMapper.update(auth, new EntityWrapper<TUserAuth>().eq("uid", userAuthAppealSubmitReq.getUid()));
            if (0 == updateAuthRes) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).msg("审核操作有误").build();
            }
        }
        if (3 == userAuthAppealSubmitReq.getAppealStatus()) {
            //申诉审核通过，申诉状态修改，实名认证表真实姓名字段修改、实名审核状态修改为通过
            Integer updateAppealRes = userAuthAppealMapper.update(authAppeal, new EntityWrapper<TUserAuthAppeal>().eq("uid", userAuthAppealSubmitReq.getUid()));
            if (0 == updateAppealRes) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).msg("审核操作有误").build();
            }
            auth.setRealName(userAuthAppealSubmitReq.getAppealRealName());
            Integer updateAuthRes = userAuthMapper.update(auth, new EntityWrapper<TUserAuth>().eq("uid", userAuthAppealSubmitReq.getUid()));
            if (0 == updateAuthRes) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).msg("审核操作有误").build();
            }
        }
        return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).build();
    }
}
