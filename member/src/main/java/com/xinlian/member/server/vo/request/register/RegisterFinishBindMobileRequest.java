package com.xinlian.member.server.vo.request.register;

import lombok.Data;

/**
 * @author Song
 * @date 2020-08-12 16:28
 * @description 注册完成绑定手机号请求类
 */
@Data
public class RegisterFinishBindMobileRequest {

    //手机号码
    private String mobile;
    //验证码
    private String mobileSmsCode;
    //登录名
    private String userName;
    //节点id
    private Long nodeId;
}
