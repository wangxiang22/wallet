package com.xinlian.admin.biz.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.xinlian.admin.biz.redis.RedisClient;
import com.xinlian.admin.biz.service.UserInfoService;
import com.xinlian.biz.dao.TChainOwnerMapper;
import com.xinlian.biz.dao.TUserAuthMapper;
import com.xinlian.biz.dao.TUserInfoMapper;
import com.xinlian.biz.dao.TWalletInfoMapper;
import com.xinlian.biz.model.TChainOwner;
import com.xinlian.biz.model.TUserAuth;
import com.xinlian.biz.model.TUserInfo;
import com.xinlian.biz.model.TWalletInfo;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.redis.RedisKeys;
import com.xinlian.common.request.FreezeUserReq;
import com.xinlian.common.request.UpdUserInfoReq;
import com.xinlian.common.request.UserInfoManagerReq;
import com.xinlian.common.request.UserListReq;
import com.xinlian.common.response.*;
import com.xinlian.common.result.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserInfoServiceImpl implements UserInfoService {
    private static Logger logger = LoggerFactory.getLogger(UserInfoServiceImpl.class);

    /**
     * 系统账户uid列表
     */
    private static final List<Long> systemIds = new ArrayList<>(Arrays.asList(1L,490L,284409L,285337L,288127L,288234L,292730L,311103L,311125L,311285L));

    @Autowired
    private TUserInfoMapper userInfoMapper;

    @Autowired
    private TWalletInfoMapper walletInfoMapper;

    @Autowired
    private TUserAuthMapper userAuthMapper;

    @Autowired
    private TChainOwnerMapper chainOwnerMapper;

    @Autowired
    private RedisClient redisClient;

    @Override
    public PageResult<List<UserInfoRes>> userInfoList(UserListReq req){
        PageResult<List<UserInfoRes>> result = new PageResult<>();
        EntityWrapper<TUserInfo> wrapper = new EntityWrapper<>();
        fillParam(req, wrapper);
        result.setTotal(userInfoMapper.selectCount(wrapper));
        wrapper.last("limit " + req.pickUpOffset() + "," + req.pickUpPageSize());
        List<TUserInfo> list =  userInfoMapper.selectList(wrapper);
        List<UserInfoRes> resList = new ArrayList<>(list.size());
        list.stream().forEach(e -> {
            resList.add(e.userInfoRes());
        });
        //实名认证信息
        fillUserAuth(resList);
        //是否 是链权人
        resList.stream().forEach(i->{
            //查询上级
            InviteUserRes inviteUserRes = userInfoMapper.selectUp(i.getParentId());
            if (inviteUserRes==null){
                inviteUserRes=new InviteUserRes();
            }
            if (null!=i.getParentId()&&i.getParentId()==1){
                inviteUserRes.setDes("系统邀请");
            }
                if (null==i.getParentId()||i.getParentId()==0){
                inviteUserRes.setDes("无上级");
            }
            i.setInviteUserRes(inviteUserRes);
            //查询下级
            List<InviteUserRes> down = userInfoMapper.selectDown(i.getUid());
            i.setByInviteUsers(down);
        });

        result.setResult(resList);
        result.setCurPage(req.pickUpCurPage());
        result.setPageSize(req.pickUpPageSize());
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        return result;
    }

    private void fillUserAuth(List<UserInfoRes> resList){
        if(resList.isEmpty()){
            return;
        }
        List<Long> list = new ArrayList<>(resList.size());
        resList.stream().forEach(e -> {
            list.add(e.getUid());
        });
        //实名认证
        List<TUserAuth> authList = userAuthMapper.selectList(new EntityWrapper<TUserAuth>().in("uid", list));
        Map<Long, TUserAuth> authMap = authList.stream().collect(Collectors.toMap(TUserAuth::getUid, e->e));
        //链权人
        List<TChainOwner> ownerList = chainOwnerMapper.selectList(new EntityWrapper<TChainOwner>().in("uid", list));
        Map<Long,TChainOwner> ownerMap = ownerList.stream().collect(Collectors.toMap(TChainOwner::getUid, e->e));
        for(UserInfoRes res : resList){
            res.setRealAuthStatus(0);
            TUserAuth auth = authMap.get(res.getUid());
            if(auth != null){
                res.setRealAuthStatus(auth.getStatus().intValue() == 3 ? 1 : 0);
                res.setRealName(auth.getRealName());
                res.setIdNo(auth.getAuthSn());
            }
            TChainOwner owner = ownerMap.get(res.getUid());
            if(owner != null){
                res.setChainOwner(1);
            }
        }
    }

    private void fillParam(UserListReq req, EntityWrapper<TUserInfo> wrapper){
        //System.out.println(JSON.toJSONString(req));
        if(req.getLevelStatus() != null){
            wrapper.eq("level_status", req.getLevelStatus());
        }
        if(req.getServerNodeId() != null){
            wrapper.eq("server_node_id", req.getServerNodeId());
        }
        if(req.getParamType() != null){
            switch (req.getParamType().intValue()){
                case 1:
                    wrapper.eq("user_name", req.getParamValue());
                    break;
                case 2:
                    wrapper.eq("uid", Long.valueOf(req.getParamValue()));
                    break;
                case 3:
                    wrapper.eq("mobile", req.getParamValue());
                    break;
                case 4:
                    wrapper.eq("email", req.getParamValue());
                    break;
                case 5:
                    wrapper.eq("real_name", req.getParamValue());
                    break;
                case 6:
                    wrapper.eq("invitation_code", req.getParamValue());
                    break;
                default:
                    ;
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResponseResult freezeUser(FreezeUserReq req){
        if(StringUtils.isEmpty(req.getFreezeReson())){
            throw new BizException("请填写客服所看到的冻结原因");
        }
        if(StringUtils.isEmpty(req.getShowFreezeReason())){
            throw new BizException("请填写客户所看到的冻结原因");
        }
        ResponseResult result = new ResponseResult();
        TUserInfo info = new TUserInfo();
        info.setLevelStatus(0);
        info.setUpdateor(req.getUid());
        info.setUpdateTime(new Date());
        info.setFreezeReson(req.getFreezeReson());
        info.setShowFreezeReason(req.getShowFreezeReason());
        int count = userInfoMapper.update(info, new EntityWrapper<TUserInfo>()
                .eq("uid", req.getId()).gt("level_status", 0));
        if(count > 0){
            pushFreezeToRedis(req.getId());
        }
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        return result;
    }

    private void pushFreezeToRedis(Long uid){
        String key = RedisKeys.freezeUserKey(uid);
        for(int i=0; i<20; i++){
            try{
                redisClient.set(key, uid);
                redisClient.expire(key, 24 * 60 * 60);
                return;
            }catch(Exception e){
                logger.error(e.getMessage(), e);
            }
        }
        throw new RuntimeException("冻结用户写入缓存异常");
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResponseResult unfreezeUser(FreezeUserReq req){
        ResponseResult result = new ResponseResult();
        TUserInfo info = new TUserInfo();
        //info.setUid(req.getId());
        info.setLevelStatus(1);
        info.setUpdateor(req.getUid());
        info.setUpdateTime(new Date());
        info.setFreezeReson(req.getFreezeReson());
        userInfoMapper.update(info, new EntityWrapper<TUserInfo>()
                .eq("uid", req.getId()));
        redisClient.deleteByKey(RedisKeys.freezeUserKey(req.getId()));
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        return result;
    }


    @Override
    public ResponseResult<UserInfoDetailRes> userInfoDetail(long uid){
        ResponseResult<UserInfoDetailRes> result = new ResponseResult<>();
        TUserInfo userInfo =  userInfoMapper.selectById(uid);
        UserInfoDetailRes res = userInfo.userInfoDetailRes();
        //用户 钱包信息
        List<TWalletInfo> list = walletInfoMapper.selectList(new EntityWrapper<TWalletInfo>().eq("uid", uid));
        List<CurrencyInfoRes> resList = new ArrayList<>(list.size());
        list.stream().forEach(e -> {
            resList.add(e.currencyInfoRes());
        });
        res.setWalletInfos(resList);
        //实名 认证信息
        fillUserAuth(res);
        result.setResult(res);
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        return result;
    }

    private void fillUserAuth(UserInfoDetailRes res){
        List<TUserAuth> authList = userAuthMapper.selectList(new EntityWrapper<TUserAuth>().eq("uid", res.getUid()));
        if(authList.isEmpty()){
            res.setRealAuthStatus(0);
            return;
        }
        TUserAuth auth = authList.get(0);
        res.setRealAuthStatus(auth.getStatus().intValue() == 3 ? 1 : 0);
        res.setRealName(auth.getRealName());
        res.setIdNo(auth.getAuthSn());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResponseResult updateUser(UpdUserInfoReq req, Long updateor){
        ResponseResult result = new ResponseResult();
        //修改用户信息
        TUserInfo userInfo = fillUserParam(req);
        userInfoMapper.updateById(userInfo);
        if(req.getLevelStatus() != null && req.getLevelStatus() == 0){
            FreezeUserReq freezeReq = new FreezeUserReq();
            freezeReq.setUid(updateor);
            freezeReq.setId(req.getUid());
            freezeUser(freezeReq);
        }
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        return result;
    }

    public TUserInfo fillUserParam(UpdUserInfoReq req){
        TUserInfo info = new TUserInfo();
        info.setUid(req.getUid());
        if(StringUtils.isNotBlank(req.getPhone())){
            info.setMobile(req.getPhone());
        }
        if(StringUtils.isNotBlank(req.getEmail())){
            info.setEmail(req.getEmail());
        }
        if(req.getLevelStatus() != null){
            info.setLevelStatus(req.getLevelStatus());
        }
        if(StringUtils.isNotBlank(req.getNickName())){
            info.setNickName(req.getNickName());
        }
        TUserAuth userAuth = null;
        if(StringUtils.isNotBlank(req.getRealName())){
            info.setRealName(req.getRealName());
            userAuth = new TUserAuth();
            userAuth.setRealName(req.getRealName());
        }
        if(StringUtils.isNotBlank(req.getIdNo())){
            info.setIdNo(req.getIdNo());
            if(userAuth == null){
                userAuth = new TUserAuth();
            }
            userAuth.setAuthSn(req.getIdNo());
        }
        if(userAuth != null){
            userAuthMapper.update(userAuth, new EntityWrapper<TUserAuth>().eq("uid", req.getUid()));
        }
        info.setUpdateTime(new Date());
        return info;
    }

    @Override
    public PageResult<List<UserInfoManagerRes>> queryUserListPage(UserInfoManagerReq userInfoManagerReq) {
        try {
            PageResult<List<UserInfoManagerRes>> result = new PageResult<>();
            if (checkObjectFieldIsNull(userInfoManagerReq)) {
                result.setTotal(userInfoMapper.queryUserListCount(userInfoManagerReq));
            }else{
                result.setTotal(userInfoMapper.selectCount(null));
            }
            userInfoManagerReq.setStartIndex(Integer.parseInt(String.valueOf(userInfoManagerReq.pickUpOffset())));
            userInfoManagerReq.setPageSize(userInfoManagerReq.pickUpPageSize());
            List<UserInfoManagerRes> userInfoManagerResList = userInfoMapper.queryUserList(userInfoManagerReq);
            if (null != userInfoManagerResList && userInfoManagerResList.size() > 0) {
                for (UserInfoManagerRes userInfoManagerRes : userInfoManagerResList) {
                    //判断是否为链权人
                    TChainOwner tChainOwner = new TChainOwner();
                    tChainOwner.setUid(userInfoManagerRes.getUid());
                    TChainOwner chainOwner = chainOwnerMapper.selectOne(tChainOwner);
                    userInfoManagerRes.setChainOwner(null == chainOwner? 0:1);
                    //查询邀请人数
                    Long invitationNum = userInfoMapper.queryUserInvitationNum(userInfoManagerRes.getUid());
                    userInfoManagerRes.setInvitationNum(invitationNum);
                }
                result.setResult(userInfoManagerResList);
            }
            result.setCurPage(userInfoManagerReq.pickUpCurPage());
            result.setPageSize(userInfoManagerReq.pickUpPageSize());
            result.setCode(GlobalConstant.ResponseCode.SUCCESS);
            return result;
        } catch (Exception e) {
            log.error("异常信息：",e);
            throw new BizException("系统繁忙");
        }
    }

    @Override
    public ResponseResult<UserInfoInvitationRes> queryUserInvitation(Long uid) {
        ResponseResult<UserInfoInvitationRes> result = new ResponseResult<>();
        TUserInfo userInfo = userInfoMapper.selectById(uid);
        if (null == userInfo) {
            result.setCode(GlobalConstant.ResponseCode.FAIL);
            result.setMsg("操作失败");
            return result;
        }
        UserInfoInvitationRes invitationRes = new UserInfoInvitationRes();
        invitationRes.setUid(uid);
        invitationRes.setUserName(userInfo.getUserName());
        invitationRes.setHeadPortraitUrl(userInfo.getHeadPortraitUrl());
        invitationRes.setInvitationNum(userInfoMapper.queryUserInvitationNum(uid));
        //查找该用户的上级邀请人
        InviteUserRes inviteUserRes = userInfoMapper.selectUp(userInfo.getParentId());
        if (null == inviteUserRes) {
            inviteUserRes = new InviteUserRes();
        }
        if (null == userInfo.getParentId() || 0 == userInfo.getParentId()) {
            inviteUserRes.setDes("无上级");
        }
        for (Long systemId : systemIds) {
            if (systemId.equals(userInfo.getParentId())) {
                inviteUserRes.setDes("系统邀请");
            }
        }
        invitationRes.setInviteUserRes(inviteUserRes);
        //查找该用户的下级关系列表
        invitationRes.setByInviteUsers(userInfoMapper.selectDown(uid));
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        result.setResult(invitationRes);
        return result;
    }

    @Override
    public ResponseResult<WalletFindUserRes> queryUserByWallet(int addressType,String currencyAddress) {
        ResponseResult<WalletFindUserRes> result = new ResponseResult<>();
        if (null == currencyAddress || "".equals(currencyAddress)){
            result.setCode(GlobalConstant.ResponseCode.FAIL);
            result.setMsg("请输入钱包地址！");
            return result;
        }
        WalletFindUserRes walletFindUserRes = null;
        //地址类型 - 0：旧优盾地址，1：新钱包地址  - 只区分TRC地址
        if (currencyAddress.substring(0, 2).equalsIgnoreCase("0x")
                && currencyAddress.length() == 42) {
            walletFindUserRes = userInfoMapper.queryUserByNewWallet(currencyAddress);
        }else {
            walletFindUserRes = userInfoMapper.queryUserByTrcWallet(currencyAddress);
        }
        if (null == walletFindUserRes) {
            result.setCode(GlobalConstant.ResponseCode.FAIL);
            result.setMsg("未查找到用户信息！");
            return result;
        }
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        result.setResult(walletFindUserRes);
        return result;
    }

    /**
     * 验证对象类字段是否全部为空，全部为空返回false
     * @param object
     * @param excludeFields 排除判断字段
     * @return
     */
    private boolean checkObjectFieldIsNull(Object object,String ... excludeFields) {
        Class objClass = object.getClass();
        Field[] fields = objClass.getDeclaredFields();
        Object resultFileValue = null;
        boolean flag = false;
        try {
            for (Field field : fields) {
                // 属性名称
                String currentFieldName = field.getName();
                field.setAccessible(true);
                resultFileValue = field.get(object);
                for(String f : excludeFields){
                    if(currentFieldName.toUpperCase().equals(f.toUpperCase())){continue;}
                }
                if(resultFileValue!=null){
                    flag = true;
                    continue;
                }
            }
        }catch(Exception e){
            log.error("checkObjectFieldIsNull反射出现异常:", e.toString(), e);
        }
        return flag;
    }
}
