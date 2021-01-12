package com.xinlian.admin.biz.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.xinlian.admin.biz.service.TRecommendService;
import com.xinlian.biz.dao.TRecommendMapper;
import com.xinlian.biz.model.TRecommend;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wx
 * @since 2020-03-25
 */
@Service
public class TRecommendServiceImpl extends ServiceImpl<TRecommendMapper, TRecommend> implements TRecommendService {

    @Autowired
    private TRecommendMapper tRecommendMapper;

    @Override
    public ResponseResult queryRecommend() {
        ResponseResult result=new ResponseResult();

        List<TRecommend> tRecommends = tRecommendMapper.selectList(new EntityWrapper<TRecommend>().orderDesc(Collections.singletonList("sort")));
        if (tRecommends.size()==0 || null==tRecommends){
            result.setMsg("请求成功，暂无数据");
            result.setCode(GlobalConstant.ResponseCode.SUCCESS);
            return result;
        }
        result.setMsg("请求成功");
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        result.setResult(tRecommends);
        return result;
    }

    @Transactional
    @Override
    public ResponseResult deleteRecommend(int id) {
        ResponseResult result=new ResponseResult();
        Integer integer = tRecommendMapper.deleteById(id);
        if (integer<=0){
            result.setMsg("删除失败");
            result.setCode(GlobalConstant.ResponseCode.FAIL);
        }
        result.setMsg("请求成功");
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        result.setResult(integer);
        return result;
    }

    @Transactional
    @Override
    public ResponseResult updateRecommend(TRecommend tRecommend) {
        ResponseResult result=new ResponseResult();
        Integer integer = tRecommendMapper.updateById(tRecommend);
        if (integer<=0){
            result.setMsg("更新失败");
            result.setCode(GlobalConstant.ResponseCode.FAIL);
        }
        result.setMsg("请求成功");
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        result.setResult(integer);
        return result;
    }

    @Transactional
    @Override
    public ResponseResult insertRecommend(TRecommend tRecommend) {
        ResponseResult result=new ResponseResult();
        Integer integer = tRecommendMapper.insert(tRecommend);
        if (integer<=0){
            result.setMsg("新增失败");
            result.setCode(GlobalConstant.ResponseCode.FAIL);
        }
        result.setMsg("请求成功");
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        result.setResult(integer);
        return result;
    }

}
