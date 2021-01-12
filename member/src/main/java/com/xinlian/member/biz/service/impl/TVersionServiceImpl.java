package com.xinlian.member.biz.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.xinlian.biz.dao.TVersionMapper;
import com.xinlian.biz.model.TVersion;
import com.xinlian.common.enums.ErrorCode;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.service.TVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wjf
 * @since 2020-01-02
 */
@Service
public class TVersionServiceImpl extends ServiceImpl<TVersionMapper, TVersion> implements TVersionService {
@Autowired
private TVersionMapper tVersionMapper;
    @Override
    public ResponseResult queryOne() {
        return ResponseResult.builder().code(ErrorCode.REQ_SUCCESS.getCode()).msg(ErrorCode.REQ_SUCCESS.getDes()).result(tVersionMapper.queryOne()).build();
    }
}
