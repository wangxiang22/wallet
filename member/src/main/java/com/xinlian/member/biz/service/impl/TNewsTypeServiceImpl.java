package com.xinlian.member.biz.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.xinlian.biz.dao.TNewsTypeMapper;
import com.xinlian.biz.model.TNewsType;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.service.TNewsTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * banner图表 服务实现类
 * </p>
 *
 * @author wx
 * @since 2020-03-04
 */
@Service
public class TNewsTypeServiceImpl extends ServiceImpl<TNewsTypeMapper, TNewsType> implements TNewsTypeService {

    @Autowired
    private TNewsTypeMapper tNewsTypeMapper;


    @Override
    public ResponseResult queryAll() {
        ResponseResult<Object> result = new ResponseResult<>();
        List<TNewsType> tNewsTypes = tNewsTypeMapper.selectList(new EntityWrapper<>());
        result.setMsg("请求成功");
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        result.setResult(tNewsTypes);
        return result;
    }


}
