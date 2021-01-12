package com.xinlian.admin.biz.service.impl;

import com.xinlian.admin.biz.service.OlddataDrMemberService;
import com.xinlian.biz.model.OlddataDrMember;
import com.xinlian.biz.dao.OlddataDrMemberMapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.xinlian.common.response.QueryAddress;
import com.xinlian.common.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *
 * </p>
 *
 * @author WX
 * @since 2020-05-16
 */
@Service
public class OlddataDrMemberServiceImpl extends ServiceImpl<OlddataDrMemberMapper, OlddataDrMember> implements OlddataDrMemberService {


    @Autowired
    private  OlddataDrMemberMapper olddataDrMemberMapper;
    @Override
    public ResponseResult searchAddress(QueryAddress queryAddress) {
        ResponseResult<Object> result = new ResponseResult<>();
        QueryAddress address = olddataDrMemberMapper.searchAddress(queryAddress.getUid(), queryAddress.getFaddress(),queryAddress.getUsername());
        if (null==address || "".equals(address)){
            result.setMsg("没有信息");
            return result;
        }
        result.setCode(200);
        result.setMsg("请求成功");
        result.setResult(address);
        return result;
    }
}
