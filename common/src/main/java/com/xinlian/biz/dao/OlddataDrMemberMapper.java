package com.xinlian.biz.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xinlian.biz.model.OlddataDrMember;
import com.xinlian.common.response.QueryAddress;

/**
 * <p>
 *
 * </p>
 *
 * @author WX
 * @since 2020-05-16
 */
public interface OlddataDrMemberMapper extends BaseMapper<OlddataDrMember> {

    QueryAddress  searchAddress(Integer uid,String faddress,String username);
}
