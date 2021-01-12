package com.xinlian.admin.biz.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.xinlian.admin.biz.service.TNewsTypeService;
import com.xinlian.biz.model.TNewsType;
import com.xinlian.biz.dao.TNewsTypeMapper;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * banner图表 服务实现类
 * </p>
 *
 * @author wx
 * @since 2020-03-03
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

    @Transactional
    @Override
    public ResponseResult deleteById(Long id) {
        ResponseResult<Object> result = new ResponseResult<>();
        Integer num = tNewsTypeMapper.deleteById(id);
        if (num==0){
            result.setCode(GlobalConstant.ResponseCode.FAIL);
            result.setMsg("删除失败");
            return result;
        }
        result.setMsg("删除成功");
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        result.setResult(num);
        return result;
    }

    @Transactional
    @Override
    public ResponseResult insertOne(TNewsType tNewsType) {
        ResponseResult<Object> result = new ResponseResult<>();
        Integer num = tNewsTypeMapper.insert(tNewsType);
        if (num==0){
            result.setCode(GlobalConstant.ResponseCode.FAIL);
            result.setMsg("添加失败");
            return result;
        }
        result.setMsg("添加成功");
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        result.setResult(num);
        return result;
    }

    @Transactional
    @Override
    public ResponseResult updateNewsType(TNewsType tNewsType) {
        ResponseResult<Object> result = new ResponseResult<>();
        Integer num = tNewsTypeMapper.updateById(tNewsType);
        if (num==0){
            result.setCode(GlobalConstant.ResponseCode.FAIL);
            result.setMsg("更新失败");
            return result;
        }
        result.setMsg("更新成功");
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        result.setResult(num);
        return result;
    }
}
