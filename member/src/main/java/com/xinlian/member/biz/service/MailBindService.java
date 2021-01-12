package com.xinlian.member.biz.service;

import com.xinlian.common.request.BindMailReq;
import com.xinlian.common.response.ResponseResult;

public interface MailBindService {
    /**
     * 判断邮箱是否已绑定其他账号
     * @param email 邮箱
     * @param nodeId 节点id
     * @return 0：符合绑定要求，1：国内用户绑定不合规时的情况，2：大航海用户绑定不合规时的情况
     */
    int findEmailExists(String email,Long nodeId);
    /**
     * 绑定邮箱
     * @param bindMailReq
     * @return
     */
    ResponseResult bindMail(BindMailReq bindMailReq);
}
