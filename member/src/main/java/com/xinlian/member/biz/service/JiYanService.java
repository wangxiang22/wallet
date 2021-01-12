package com.xinlian.member.biz.service;

import com.xinlian.common.request.GeetestVo;
import com.xinlian.common.response.ResponseResult;

public interface JiYanService {
    ResponseResult register(GeetestVo geetestVo);

    ResponseResult validate(GeetestVo geetestVo);
}
