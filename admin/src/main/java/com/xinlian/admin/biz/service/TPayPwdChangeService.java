package com.xinlian.admin.biz.service;

import com.baomidou.mybatisplus.service.IService;
import com.xinlian.biz.model.TPayPwdChange;
import com.xinlian.common.dto.PayPwdChangeDto;
import com.xinlian.common.request.CheckUserAuthReq;
import com.xinlian.common.response.PageResult;
import com.xinlian.common.response.ResponseResult;

import java.util.List;

/**
 * <p>
 * 支付密码变更表（用户手机号已不可用） 服务类
 * </p>
 *
 * @author lx
 * @since 2020-06-04
 */
public interface TPayPwdChangeService extends IService<TPayPwdChange> {

    PageResult<List<PayPwdChangeDto>> queryList(CheckUserAuthReq checkUserAuthReq);

    ResponseResult passOrRefuse(TPayPwdChange checkUserAuthReq);
}
