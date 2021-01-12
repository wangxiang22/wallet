package com.xinlian.admin.biz.service;

import com.xinlian.common.request.FreezeUserReq;
import com.xinlian.common.request.UpdUserInfoReq;
import com.xinlian.common.request.UserInfoManagerReq;
import com.xinlian.common.request.UserListReq;
import com.xinlian.common.response.*;

import java.util.List;

public interface UserInfoService {

    /**
     * 会员列表
     * @param req
     * @return
     */
    PageResult<List<UserInfoRes>> userInfoList(UserListReq req);

    /**
     * 冻结会员
     * @param req
     * @return
     */
    ResponseResult freezeUser(FreezeUserReq req);

    /**
     *
     * @param req
     * @return
     */
    ResponseResult unfreezeUser(FreezeUserReq req);

    /**
     * 用户信息 详情
     * @param uid
     * @return
     */
    ResponseResult<UserInfoDetailRes> userInfoDetail(long uid);

    /**
     *
     * @param req
     * @param updateor
     * @return
     */
    ResponseResult updateUser(UpdUserInfoReq req, Long updateor);

    /**
     * 分页查询用户信息
     * @param userInfoManagerReq
     * @return
     */
    PageResult<List<UserInfoManagerRes>> queryUserListPage(UserInfoManagerReq userInfoManagerReq);

    /**
     * 查询用户上下级
     * @param uid
     * @return
     */
    ResponseResult<UserInfoInvitationRes> queryUserInvitation(Long uid);

    /**
     * 通过钱包地址查询用户信息
     * @param addressType 地址类型 - 0：旧优盾地址，1：新钱包地址
     * @param currencyAddress 钱包地址
     * @return
     */
    ResponseResult<WalletFindUserRes> queryUserByWallet(int addressType,String currencyAddress);
}
