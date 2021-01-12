package com.xinlian.member.biz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.xinlian.biz.dao.TUserAuthMapper;
import com.xinlian.biz.dao.TUserInfoMapper;
import com.xinlian.biz.model.TPayPwdChange;
import com.xinlian.biz.model.TServerNode;
import com.xinlian.biz.model.TUserAuth;
import com.xinlian.biz.model.TUserInfo;
import com.xinlian.biz.utils.AdminOptionsUtil;
import com.xinlian.biz.utils.NodeVoyageUtil;
import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
import com.xinlian.common.enums.CommonEnum;
import com.xinlian.common.enums.ErrorCode;
import com.xinlian.common.request.CheckUserAuthReq;
import com.xinlian.common.request.UserAuthenticationReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.response.SystemSwitchRes;
import com.xinlian.common.response.UserAuthAgeBooleanRes;
import com.xinlian.common.response.UserAuthAgeRes;
import com.xinlian.common.result.BizException;
import com.xinlian.common.result.ErrorInfoEnum;
import com.xinlian.common.utils.DateFormatUtil;
import com.xinlian.common.utils.SystemUtils;
import com.xinlian.member.biz.jwt.util.EncryptionUtil;
import com.xinlian.member.biz.jwt.util.JwtUtil;
import com.xinlian.member.biz.service.IServerNodeService;
import com.xinlian.member.biz.service.IUserAuthenticationService;
import com.xinlian.member.biz.service.TPayPwdChangeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class UserAuthenticationServiceImpl implements IUserAuthenticationService {

    @Autowired
    private TUserAuthMapper tUserAuthMapper;
    @Autowired
    private IServerNodeService serverNodeService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AdminOptionsUtil adminOptionsUtil;
    @Autowired
    private TPayPwdChangeService tPayPwdChangeService;
    @Autowired
    private TUserInfoMapper tUserInfoMapper;
    @Autowired
    private NodeVoyageUtil nodeVoyageUtil;

    @Override
    public ResponseResult toAuth(UserAuthenticationReq userAuthenticationReq, HttpServletRequest httpServletRequest) {
        //1：国内扫描方式（中国大陆） 2：其他地区方式（海外）
        Integer from = userAuthenticationReq.getFrom();
        //获取登录用户的id
        Long userId = jwtUtil.getUserId(httpServletRequest);
        //大航海节点下如果没有绑定手机号码，请让先绑定手机号码
        if(nodeVoyageUtil.belongVoyageNode(userAuthenticationReq.getNode())){
            TUserInfo getUserModel = tUserInfoMapper.selectById(userId);
            if(StringUtils.isEmpty(getUserModel.getMobile())){
                throw new BizException("请先绑定手机号码!");
            }
        }
        //获取查询是否已有实名信息
        TUserAuth isAuth = new TUserAuth();
        isAuth.setUid(userId);
        TUserAuth res = tUserAuthMapper.selectOne(isAuth);
        if (res!=null){
            if (res.getStatus()==3){
                throw new BizException("您已通过实名验证！！！");
            }
            if (res.getStatus()==1){
                throw new BizException("您已有实名请求正在处理中！！！");
            }
        }
        //针对矿机已经激活，但是没有提交实名认证及实名认证被驳回的会员需要重新认证的情况。这样的会员不限制实名次数和年龄。
        List<Long> activeNotAuthList = tUserAuthMapper.findActiveNotAuthList(userId);
        if (null == activeNotAuthList || activeNotAuthList.size() == 0 || !activeNotAuthList.contains(userId)) {
            if (1 == from) {
                //国内身份证年龄判断
                UserAuthAgeBooleanRes userAuthAgeBooleanRes = this.authAge(userAuthenticationReq.getAuth_sn());
                if (null != userAuthAgeBooleanRes && !userAuthAgeBooleanRes.isFlag()) {
                    return ResponseResult.builder().code(ErrorInfoEnum.PARAM_ERR.getCode())
                            .msg("请使用" + userAuthAgeBooleanRes.getAuthMinAge() + "周岁到" + userAuthAgeBooleanRes.getAuthMaxAge() + "周岁的身份证进行实名！").build();
                }
            }
            //获取用户所在节点信息
            TServerNode queryResult = serverNodeService.getById(userAuthenticationReq.getNode());
            if(null == queryResult) {
                throw new BizException("不存在此节点信息！");
            }
            if (0 == queryResult.getAuthStatus()){
                throw new BizException("当前节点不可认证！");
            }
            //判断全局开关是否打开，开了则走全局配置，没开就走节点单独配置
            try {
                SystemSwitchRes systemSwitchRes =
                        adminOptionsUtil.fieldEntityObject(AdminOptionsBelongsSystemCodeEnum.SYSTEM_SWITCH.getBelongsSystemCode(), SystemSwitchRes.class);
                //全局开关不包含对大航海及其子节点的控制
                if (null != systemSwitchRes && "1".equals(systemSwitchRes.getSystemApplicationFlag()) && !nodeVoyageUtil.belongVoyageNode(userAuthenticationReq.getNode())) {
                    Integer count = tUserAuthMapper.getUserAuthingCount(userAuthenticationReq.getAuth_sn());
                    if (count >= Integer.parseInt(systemSwitchRes.getAuthRegisterAmount())) {
                        return ResponseResult.builder().code(ErrorCode.REQ_ERROR.getCode())
                                .msg("当前身份证号实名中和已实名数量超过已实名数量限制！")
                                .build();
                    }
                } else {
                    //亚历山大判断
                    if (userAuthenticationReq.getNode() == 110L) {
                        Integer notBelongAlexandriaCount = tUserAuthMapper.getNotBelongAlexandriaCount(userAuthenticationReq.getAuth_sn());
                        //查看身份证号是否在别的节点正在审核或已通过审核，如是则实名驳回
                        if (notBelongAlexandriaCount > 0) {
                            throw new BizException("该身份证号已在其他节点认证！");
                        }
                    }
                    //同节点同身份证号实名个数不得超过该节点的配置，如果节点配置是0则表示不限制数量
                    Integer sameNodeAuthCount = tUserAuthMapper.getSameNodeAuthCount(userAuthenticationReq.getAuth_sn(), userAuthenticationReq.getNode());
                    if (sameNodeAuthCount >= queryResult.getAuthRegisterAmount() && queryResult.getAuthRegisterAmount() != 0) {
                        return ResponseResult.builder().code(ErrorCode.REQ_ERROR.getCode())
                                .msg("您的身份证号已在当前节点实名" + queryResult.getAuthRegisterAmount() + "个及以上不可再实名")
                                .result(new JSONObject()).build();
                    }
                }
            }catch (BizException e){
                return new ResponseResult(e);
            }catch (Exception e){
                log.error("查找全局配置出现异常：{}",e.toString(),e);
                return new ResponseResult(new BizException("认证出现异常，请稍后重试!"));
            }
        }

        TUserAuth tUserAuth = new TUserAuth();
        tUserAuth.setNode(userAuthenticationReq.getNode());
        tUserAuth.setUsername(userAuthenticationReq.getUserName());
        tUserAuth.setAuthSn(userAuthenticationReq.getAuth_sn());
        tUserAuth.setUid(jwtUtil.getUserId(httpServletRequest));
        tUserAuth.setAuthScsfz(userAuthenticationReq.getScsfz());
        tUserAuth.setAuthSfzfm(userAuthenticationReq.getSfzfm());
        tUserAuth.setAuthSfzzm(userAuthenticationReq.getSfzzm());
        tUserAuth.setRealName(userAuthenticationReq.getAuth_name());
        tUserAuth.setFrom(from);
        tUserAuth.setInputTime(System.currentTimeMillis()/1000);
        tUserAuth.setInputIp(SystemUtils.getIpAddress(httpServletRequest));
        //如果是国内用户
        if (from == 1) {
            //审核成功
            //tUserAuth.setStatus(3);
            tUserAuth.setStatus(1);
        } else {
            //如果是海外用户需要人工审核
            //审核中
            tUserAuth.setStatus(1);
        }
        //如果是之前拒绝的数据，修改即可
        if (res != null && res.getStatus() == 2){
            tUserAuthMapper.update(tUserAuth,new EntityWrapper<TUserAuth>().eq("uid",res.getUid()));
            return ResponseResult.builder().code(ErrorCode.REQ_SUCCESS.getCode()).msg(ErrorCode.REQ_SUCCESS.getDes()).result(new JSONObject()).build();
        }
        tUserAuthMapper.insert(tUserAuth);
        return ResponseResult.builder().code(ErrorCode.REQ_SUCCESS.getCode()).msg(ErrorCode.REQ_SUCCESS.getDes()).result(new JSONObject()).build();
    }

    /**
     * 国内身份证年龄判断
     * @param authSn 身份证号
     * @return
     */
    private UserAuthAgeBooleanRes authAge(String authSn) {
        UserAuthAgeRes userAuthAgeRes = new UserAuthAgeRes();
        UserAuthAgeBooleanRes userAuthAgeBooleanRes = new UserAuthAgeBooleanRes();
        try {
            userAuthAgeRes = adminOptionsUtil.fieldEntityObject(AdminOptionsBelongsSystemCodeEnum.APP_AUTH_AGE.getBelongsSystemCode(), UserAuthAgeRes.class);
            if (null != userAuthAgeRes) {
                userAuthAgeBooleanRes = userAuthAgeRes.userAuthAgeBooleanRes();
                //根据身份证号获取用户的年龄（周岁）
                Integer age = DateFormatUtil.getAgeByIdCardNo(authSn);
                int authMinAge = Integer.parseInt(userAuthAgeRes.getAuthMinAge());
                int authMaxAge = Integer.parseInt(userAuthAgeRes.getAuthMaxAge());
                if (authMinAge > age || authMaxAge < age) {
                    userAuthAgeBooleanRes.setFlag(false);
                }else {
                    userAuthAgeBooleanRes.setFlag(true);
                }
            }
        }catch (Exception e){
            log.error("获取实名认证年龄限制区间配置出现异常：{}",e.toString(),e);
        }
        return userAuthAgeBooleanRes;
    }

    @Override
    public ResponseResult getAuthState(Long uid) {
        TUserAuth tUserAuth = new TUserAuth();
        tUserAuth.setUid(uid);
        TUserAuth result = tUserAuthMapper.selectOne(tUserAuth);
        if (result==null){
            return ResponseResult.builder().code(ErrorCode.REQ_SUCCESS.getCode()).msg(CommonEnum.AUTH_NOT_FOUND.getDes()).result(result).build();
        }
        return ResponseResult.builder().code(ErrorCode.REQ_SUCCESS.getCode()).msg(ErrorCode.REQ_SUCCESS.getDes()).result(result).build();
    }

    @Override
    public ResponseResult checkUserAuth(CheckUserAuthReq checkUserAuthReq) {
        TUserAuth tUserAuth = tUserAuthMapper.checkUserAuth(checkUserAuthReq);
        if (!tUserAuth.getRealName().equals(checkUserAuthReq.getRealName())){
            throw new BizException("姓名有误，请检查后再次提交");
        }
        String authSn = tUserAuth.getAuthSn();
        String auth6 = authSn.substring(authSn.length() - 6);
        if (!checkUserAuthReq.getAuthNo6().equals(auth6)){
            throw new BizException("证件号有误，请检查后再次重新提交");
        }
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult forgetPayPwd(CheckUserAuthReq checkUserAuthReq) {
        String match = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$";
        if (!checkUserAuthReq.getPayPwd().matches(match)){
            throw new BizException("支付密码须8-16位字母加数字组合");
        }
        TPayPwdChange tPayPwdChange = new TPayPwdChange();
        tPayPwdChange.setCreateTime(new Date());
        tPayPwdChange.setUid(checkUserAuthReq.getUid());
        tPayPwdChange.setAuthNo(checkUserAuthReq.getAuthNo6());
        tPayPwdChange.setRealName(checkUserAuthReq.getRealName());
        tPayPwdChange.setAuthScsfz(checkUserAuthReq.getAuthScsfz());
        tPayPwdChange.setState(0);//设置审核状态 为审核中
        TUserInfo tUserInfo = tUserInfoMapper.selectById(checkUserAuthReq.getUid());
        String salt = tUserInfo.getSalt();
        String password = EncryptionUtil.md5Two(checkUserAuthReq.getPayPwd(), salt);
        if (password.equals(tUserInfo.getLoginPassWord())){
            throw new BizException("支付密码和登录密码不可相同");
        }
        if (password.equals(tUserInfo.getPayPassWord())){
            throw new BizException("支付密码和原支付密码不可相同");
        }

        tPayPwdChange.setPayPassword(EncryptionUtil.md5Two(checkUserAuthReq.getPayPwd(), salt));
        tPayPwdChange.setUserName(tUserInfo.getUserName());
        TPayPwdChange dbResult = tPayPwdChangeService.selectOne(new EntityWrapper<TPayPwdChange>()
                .eq("uid", checkUserAuthReq.getUid())
                .eq("state", 0));
        if (dbResult!=null){
            throw new BizException("您已有忘记密码申请在审核中！");
        }
        tPayPwdChangeService.insert(tPayPwdChange);
        return ResponseResult.ok();
    }
}
