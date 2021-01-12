package com.xinlian.common.request;

import com.xinlian.common.result.BizException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterReq implements ICheckParam {
    // 区域
    private Integer countryCode;
    // 节点
    private Long nodeId;
    private String phone;
    private String username;
    // 验证码
    private String code;
    private String password;
    private String password2;
    // 交易密码
    private String dealPsw;
    // 交易密码
    private String dealPsw2;
    // 邀请码
    private String inviteCode;
    //
    private int type;
    // 邮箱
    private String email;
    // 省份名称
    private String provinceName;
    // 城市名称
    private String cityName;

    // 待检验数值
    private String adPercent;

    @Override
    public void checkParam() {
        if (countryCode == null) {
            throw new BizException("国家区号不能为空!");
        }
        if (nodeId == null) {
            throw new BizException("节点信息不能为空!");
        }
        if (StringUtils.isBlank(phone) && StringUtils.isBlank(email)) {
            throw new BizException("手机号码不能为空!");
        }
        if (StringUtils.isBlank(username)) {
            throw new BizException("登录账号不能为空!");
        }
        if (StringUtils.isBlank(code)) {
            throw new BizException("短信验证码不能为空!");
        }
        if (StringUtils.isBlank(password)) {
            throw new BizException("登录密码不能为空!");
        }
        if (StringUtils.isBlank(dealPsw)) {
            throw new BizException("支付密码不能为空!");
        }
        if (StringUtils.isBlank(inviteCode)) {
            throw new BizException("邀请码不能为空!");
        }
    }

    public void checkSendSmsParam() {
        if (countryCode == null) {
            throwException();
        }
        if (StringUtils.isEmpty(phone)) {
            throwException();
        }
    }

    public void checkForgetPwdParam() {
        if (countryCode == null) {
            throwException();
        }
        if (nodeId == null) {
            throwException();
        }
        if (StringUtils.isBlank(phone) && StringUtils.isBlank(email)) {
            throwException();
        }
        if (StringUtils.isBlank(username)) {
            throwException();
        }
        if (StringUtils.isBlank(code)) {
            throwException();
        }
        if (StringUtils.isBlank(password)) {
            throwException();
        }
    }

    public void check4BblockmallCert() {
        if (countryCode == null) {
            throwException();
        }
        if (nodeId == null) {
            throwException();
        }
        if (StringUtils.isBlank(phone)) {
            throwException();
        }
        if (StringUtils.isBlank(username)) {
            throwException();
        }
        if (StringUtils.isBlank(code)) {
            throwException();
        }
        if (StringUtils.isBlank(password)) {
            throwException();
        }
    }

}
