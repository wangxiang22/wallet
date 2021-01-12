package com.xinlian.member.biz.service;

import com.xinlian.biz.model.TServerNode;
import com.xinlian.biz.model.TUserInfo;
import com.xinlian.common.request.*;
import com.xinlian.common.response.*;

import java.util.List;

public interface IRegisterLoginService {
	/**
	 * 短信验证码
	 * 
	 * @param req
	 * @return
	 */
	ResponseResult sendRegisterSms(RegisterReq req, boolean isInland);

	/**
	 * 发送短信验证码
	 * 
	 * @return
	 */
	ResponseResult sendPhoneSms(NeedLoginSendSmsReq needLoginSendSmsReq);

	/**
	 * 节点
	 * 
	 * @return
	 */
	ResponseResult<List<NodeDicRes>> findNodeDic(ServerNodeReq serverNodeReq);

	/**
	 * 国家
	 * 
	 * @return
	 */
	ResponseResult<List<CountryDicRes>> findCountryDic();

	/**
	 * 注册
	 * 
	 * @param req
	 * @return
	 */
	ResponseResult register(RegisterReq req, boolean isInlandRegister);

	/**
	 * 查询 节点下 是否有用户名
	 * 
	 * @param req
	 * @return
	 */
	ResponseResult<Integer> findHasUserName(RegisterReq req);

	/**
	 * 登陆
	 * 
	 * @param req
	 * @return
	 */
	ResponseResult<UserInfoRes> login(LoginReq req);

	/**
	 * 忘记 密码
	 * 
	 * @param req
	 * @return
	 */
	ResponseResult forgetPwd(RegisterReq req);

	/**
	 * 修改 登录密码
	 * 
	 * @param req
	 * @return
	 */
	ResponseResult updatePwd(UpdatePwdReq req);

	/**
	 * 修改 支付密码
	 * 
	 * @param req
	 * @return
	 */
	ResponseResult updatePayPwd(UpdatePwdReq req);

	/**
	 * 忘记 支付密码
	 * 
	 * @param req
	 * @return
	 */
	ResponseResult forgetPayPwd(UpdatePwdReq req);

	/**
	 * 修改头像或昵称
	 * 
	 * @param req
	 * @return
	 */
	ResponseResult updateUser(UpdateUserReq req);

	/**
	 * 用户 推荐码
	 * 
	 * @param uid
	 * @return
	 */
	ResponseResult<UserCodeRes> findUserCode(long uid);

	/**
	 * 用户邀请的人
	 * 
	 * @param req
	 * @return
	 */
	PageResult<List<UserInfoRes>> findUserShare(IdReq req);

	/**
	 * 用户 审核信息
	 * 
	 * @param req
	 * @return
	 */
	ResponseResult<AuthenticationRes> userAuthentication(UserAuthReq req);

	/**
	 * 用户是否设置了 支付密码 0未设置 1已设置
	 * 
	 * @param uid
	 * @return
	 */
	ResponseResult<Integer> hasPayPwd(long uid);

	/**
	 * 修改 手机号
	 * 
	 * @param req
	 * @return
	 */
	ResponseResult changePhone(ChangePhoneReq req);

	/**
	 * 修改 手机号
	 * 
	 * @param changePhone
	 * @return
	 */
	ResponseResult changePhone(ChangePhoneIdcardReq changePhone);

	/**
	 * 更换手机号 审核状态
	 * 
	 * @param uid
	 * @return
	 */
	ResponseResult<Integer> changePhoneStatus(long uid);

	/**
	 * 发送 邮箱验证码
	 * 
	 * @param req
	 * @return
	 */
	ResponseResult sendMailCode(MailSendReq req);

	/**
	 * 邮箱 注册
	 * 
	 * @param req
	 * @return
	 */
	ResponseResult registerMail(RegisterReq req, TServerNode serverNode, TUserInfo parentUserInfo);

	ResponseResult registerMailToSeaPatrol(RegisterReq req, TServerNode serverNode, TUserInfo parentUserInfo);

	/**
	 * 验证 验证码
	 * 
	 * @param type
	 * @param rPhone
	 * @param rCode
	 * @return
	 */
	boolean checkCode(int type, String rPhone, String rCode);

}
