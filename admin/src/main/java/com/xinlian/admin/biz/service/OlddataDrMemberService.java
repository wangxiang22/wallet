package com.xinlian.admin.biz.service;

import com.xinlian.biz.model.OlddataDrMember;
import com.baomidou.mybatisplus.service.IService;
import com.xinlian.common.response.QueryAddress;
import com.xinlian.common.response.ResponseResult;

/**
 * <p>
 *
 * </p>
 *
 * @author WX
 * @since 2020-05-16
 */
public interface OlddataDrMemberService extends IService<OlddataDrMember> {

    ResponseResult searchAddress(QueryAddress queryAddress);

}
