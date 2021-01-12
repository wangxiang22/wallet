package com.xinlian.biz.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xinlian.biz.model.TUserAuth;
import com.xinlian.common.dto.UserUidAuthSnDto;
import com.xinlian.common.request.CheckUserAuthReq;
import com.xinlian.common.request.UserAuthQueryReq;
import com.xinlian.common.request.UserAuthUpdateReq;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface TUserAuthMapper extends BaseMapper<TUserAuth> {

    List<TUserAuth> queryAll(UserAuthQueryReq userAuthQueryReq);

    Integer takeOffer(List<Long> list);

    Integer refuse(List<Long> list);

    Integer updateByUid(UserAuthUpdateReq userAuthUpdateReq);

    Integer queryCount(UserAuthQueryReq userAuthQueryReq);

    /**
     * 获取已实名个数 - 和()
     * @param auth_sn
     * @return
     */
    Integer getUserAuthingCount(String auth_sn);

    TUserAuth checkUserAuth(CheckUserAuthReq checkUserAuthReq);

    /**
     * 不属于亚历山大节点的实名认证信息个数（包含正在审核及已审核通过的）
     * @param authSn 身份证号码
     * @return
     */
    Integer getNotBelongAlexandriaCount(String authSn);

    /**
     * 获取同节点同身份证号正在审核和已审核通过的个数
     * @param authSn 身份证号码
     * @param nodeId 节点id
     * @return 结果数
     */
    Integer getSameNodeAuthCount(String authSn,Long nodeId);

    /**
     * 查询用户是否为已激活但未提交过实名信息或实名被驳回的用户
     * @return 用户id列表
     */
    List<Long> findActiveNotAuthList(Long uid);

    /**
     * 查询用户是否为已激活但实名待审核或实名被驳回的用户
     * @return 用户id列表
     */
    List<Long> findActiveNotAuth(Long uid);

    /**
     * 根据登录账号的已通过实名的身份证号查找该证件号下实名已通过的用户id列表
     * @param uid 用户id
     * @return uid列表
     */
    List<String> findUidListByAuthSn(Long uid);

    int queryAuthStatusByUid(@Param(value = "uid") Long uid);

    /**
     * 查询已拒绝并且国内通道的实名信息uid列表（获取20200814中午十二点 - 20200819晚上23:59:59的数据）
     * @return uid列表
     */
    List<UserUidAuthSnDto> findUidListByStatus();

    /**
     * 已审核拒绝的实名信息修改为待审核
     * @param uid 用户id
     * @return 修改结果
     */
    Integer updateByStatus(Long uid);
}
