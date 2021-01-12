package com.xinlian.admin.biz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.xinlian.admin.biz.service.BindExchangeInfoService;
import com.xinlian.biz.dao.TNewsArticleMapper;
import com.xinlian.biz.dao.TRocketBindMapper;
import com.xinlian.biz.model.TNewsArticle;
import com.xinlian.biz.model.TRocketBind;
import com.xinlian.common.enums.ErrorCode;
import com.xinlian.common.request.QueryBindInfoReq;
import com.xinlian.common.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BindExchangeInfoServiceImpl extends ServiceImpl<TRocketBindMapper, TRocketBind> implements BindExchangeInfoService {
    @Autowired
    TRocketBindMapper tRocketBindMapper;

    @Override
    public ResponseResult queryBindInfo(QueryBindInfoReq queryBindInfo) {
        JSONObject jsonObject = new JSONObject();
        if (queryBindInfo==null){
            List<TRocketBind> tRocketBinds = tRocketBindMapper.selectList(new EntityWrapper<TRocketBind>());
            Integer count = tRocketBindMapper.selectCount(new EntityWrapper<TRocketBind>());
            jsonObject.put("tRocketBinds",tRocketBinds);
            jsonObject.put("count",count);
            return ResponseResult.builder().msg(ErrorCode.REQ_SUCCESS.getDes()).code(ErrorCode.REQ_SUCCESS.getCode()).result(jsonObject).build();
        }
        List<TRocketBind> tRocketBinds = tRocketBindMapper.selectList(new EntityWrapper<TRocketBind>().eq("cat_uid",queryBindInfo.getUid()));
        jsonObject.put("tRocketBinds",tRocketBinds);
        jsonObject.put("count",tRocketBinds.size());
        return ResponseResult.builder().msg(ErrorCode.REQ_SUCCESS.getDes()).code(ErrorCode.REQ_SUCCESS.getCode()).result(jsonObject).build();
    }
}
