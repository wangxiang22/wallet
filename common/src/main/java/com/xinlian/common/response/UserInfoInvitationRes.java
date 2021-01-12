package com.xinlian.common.response;

import lombok.Data;

import java.util.List;

@Data
public class UserInfoInvitationRes {
    private Long uid;//用户id
    private String userName;//用户名
    private String headPortraitUrl;//头像url
    private Long invitationNum;//已邀请人数
    private InviteUserRes inviteUserRes;//上级（邀请人，上下级实体共用）
    private List<InviteUserRes> byInviteUsers;//下级用户信息列表
}
