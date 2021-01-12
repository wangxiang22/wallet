package com.xinlian.admin.biz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.xinlian.admin.biz.service.CurrencyManagerService;
import com.xinlian.biz.dao.TCurrencyInfoMapper;
import com.xinlian.biz.dao.TCurrencyManageMapper;
import com.xinlian.biz.model.TCurrencyInfo;
import com.xinlian.biz.model.TCurrencyManage;
import com.xinlian.common.enums.ErrorCode;
import com.xinlian.common.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CurrencyManagerServiceImpl implements CurrencyManagerService {
    @Autowired
    TCurrencyManageMapper tCurrencyManageMapper;

    @Override
    public ResponseResult queryAllCurrency() {
        return ResponseResult.builder()
                .msg(ErrorCode.REQ_ERROR.getDes())
                .code(ErrorCode.REQ_SUCCESS.getCode())
                .result(tCurrencyManageMapper.selectList(new EntityWrapper<TCurrencyManage>())).build();
    }

    @Override
    public ResponseResult udpateCurrencyInfo(TCurrencyManage tCurrencyManage) {
        tCurrencyManageMapper.update(tCurrencyManage,new EntityWrapper<TCurrencyManage>().eq("id",tCurrencyManage.getId()));
        return ResponseResult.builder()
                .msg(ErrorCode.REQ_ERROR.getDes())
                .code(ErrorCode.REQ_SUCCESS.getCode())
                .result(new JSONObject()).build();
    }
}
