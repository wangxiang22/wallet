package com.xinlian.member.server.vo.request.register;

import lombok.Data;

/**
 * @author Song
 * @date 2020-08-12 16:53
 * @description 大航海绑定手机号发送短信验证码
 */
@Data
public class FinishBindMobileSendSmsRequest {

    private String mobile;

    private String userName;

    private Long nodeId;

}
