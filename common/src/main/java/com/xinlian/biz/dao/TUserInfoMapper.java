package com.xinlian.biz.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xinlian.biz.model.TUserAuth;
import com.xinlian.biz.model.TUserInfo;
import com.xinlian.biz.model.next.NextUserInfoByUIdModel;
import com.xinlian.biz.model.next.NextUserInfoModel;
import com.xinlian.common.dto.UserInfoDto;
import com.xinlian.common.request.UserInfoManagerReq;
import com.xinlian.common.response.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public interface TUserInfoMapper extends BaseMapper<TUserInfo> {

    List<UserMessageRes> queryUserName(String mobile);

    /**
     * 根据userinfo 获取一条对应model对象
     * @param userInfo
     * @return
     */
    TUserInfo getOneModel(TUserInfo userInfo);

    /**
     * 获取总注册客户数
     * @param flagToday 是否只查询当天
     * @return
     */
    Long queryGrandTotalRegisterValue(@Param("flagToday")String flagToday);

    /**
     * 累计自然客户总数
     * @param flagToday 是否只查询当天
     * @return
     */
    Long queryGrandTotalIdNosValue(@Param("flagToday")String flagToday);

    /**
     * 已激活客户总数
     * @param flagToday 是否只查询当天
     * @return
     */
    Long queryActivateTotalValue(@Param("flagToday")String flagToday);

    /**
     * 获取开始日期-结束日期内，节点注册排名
     * @param startDate
     * @param endDate
     * @return
     */
    List<ServerNodeRankResponse> serverNodeRegisterRank(@Param("startDate") String startDate,
                                                        @Param("endDate")String endDate);

    /**
     * 根据统计维度，获取对应【维度：数值】
     * @param dimensionsType
     * @return
     */
    List<NewCustomerTrendResponse> statisticsNewCustomerTrend(
            @Param("dimensionsType") String dimensionsType,
            @Param("firstDayOfWeekTimeStr") String firstDayOfWeekTimeStr,
            @Param("lastDayOfWeekTimeStr") String lastDayOfWeekTimeStr);

    /**
     * 获取开始日期-结束日期内，节点激活排名
     * @param startDate
     * @param endDate
     * @return
     */
    List<ServerNodeRankResponse> serverNodeActivateRank(@Param("startDate") String startDate,
                                     @Param("endDate")String endDate);

    /**
     * 查询用户的上级邀请人
     * @param uid
     * @return
     */
    InviteUserRes selectUp(Long uid);

    /**
     * 查询用户的下级关系列表
     * @param parentId
     * @return
     */
    List<InviteUserRes> selectDown(Long parentId);

    /**
     * 分页查询用户信息
     * @param userInfoManagerReq
     * @return
     */
    List<UserInfoManagerRes> queryUserList(UserInfoManagerReq userInfoManagerReq);

    /**
     * 分页查询用户信息总数
     * @param userInfoManagerReq
     * @return
     */
    Long queryUserListCount(UserInfoManagerReq userInfoManagerReq);

    /**
     * 查询用户已邀请人数
     * @param uid
     * @return
     */
    Long queryUserInvitationNum(Long uid);

    /**
     * 通过新钱包地址查询用户信息
     * @param currencyAddress
     * @return
     */
    WalletFindUserRes queryUserByNewWallet(String currencyAddress);

    /**
     * 通过旧钱包地址（优盾地址）查询用户信息
     * @param currencyAddress
     * @return
     */
    WalletFindUserRes queryUserByTrcWallet(String currencyAddress);

    List<UserInfoDto> queryUserState();

    List<String> findUserIdAll();

    int updateUserInfo(TUserInfo userInfo);

    QueryBuyerRes queryBuyerInfoByAddr(String addr);

    /**
     * 获取节点 手机未激活数量
     * @param serverNodeId
     * @param mobile
     * @return
     */
    int getNodePhoneStayActivateNumber(@Param(value = "serverNodeId") Long serverNodeId,
                                         @Param(value = "mobile") String mobile);
    /**
     * 获取节点 邮箱未激活数量
     * @param serverNodeId 节点
     * @param email email
     * @return
     */
    int getNodeEmailStayActivateNumber(@Param(value = "serverNodeId") Long serverNodeId,
                                       @Param(value = "email") String email);


    List<NextUserInfoModel> getFirstLevelUserInfoByAuthSn(NextUserInfoModel nextUserInfoModel);

    List<NextUserInfoModel> getNextLevelUserInfoByAuthSnList(List<NextUserInfoModel> list);

    int batchInsertTempUser(List<NextUserInfoModel> list);

    /***根据uid来查下级
     * 第一级还是根据身份号码来查，后面的子级是根据uid来过滤
     */
    List<NextUserInfoByUIdModel> getFirstLevelUserUidByAuthSn(NextUserInfoByUIdModel nextUserInfoByUIdModel);

    List<NextUserInfoByUIdModel> getNextLevelUserInfoByUidList(List<NextUserInfoByUIdModel> list);

    int batchInsertTempUserUids(List<NextUserInfoByUIdModel> list);

    /**
     * 查询所有
     */
    List<NextUserInfoByUIdModel> getAllUserNode();
}
