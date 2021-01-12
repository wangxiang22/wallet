package com.xinlian.member.biz.service;

import com.xinlian.common.response.BootScreenRes;
import com.xinlian.common.response.ResponseResult;

public interface BootScreenService {
    /**
     * 查找app启动界面url及开关状态
     * @return
     */
    ResponseResult<BootScreenRes> findBootScreen();
}
