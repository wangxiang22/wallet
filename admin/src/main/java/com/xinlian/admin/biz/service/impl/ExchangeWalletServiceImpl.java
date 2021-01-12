package com.xinlian.admin.biz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.admin.biz.service.ExchangeWalletService;
import com.xinlian.biz.dao.TUserExchangeWalletMapper;
import com.xinlian.biz.model.TRocketBind;
import com.xinlian.common.enums.ErrorCode;
import com.xinlian.common.request.FindExchangeBindStateReq;
import com.xinlian.common.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ExchangeWalletServiceImpl implements ExchangeWalletService {
    @Autowired
    private TUserExchangeWalletMapper tUserExchangeWalletMapper;

    @Override
    public ResponseResult findExchangeWalletBindState(FindExchangeBindStateReq findExchangeBindStateReq) {
        findExchangeBindStateReq=setFindExchangeBindReq(findExchangeBindStateReq);
        List<TRocketBind> tUserExchangeWallet =
                tUserExchangeWalletMapper.findExchangeWalletBindState(findExchangeBindStateReq);
        Integer count = tUserExchangeWalletMapper.queryCount(findExchangeBindStateReq);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("list",tUserExchangeWallet);
        jsonObject.put("count",count);
        return ResponseResult.builder().result(jsonObject)
                .code(ErrorCode.REQ_SUCCESS.getCode())
                .msg(ErrorCode.REQ_SUCCESS.getDes()).build();
    }

    @Override
    public ResponseResult queryAllRecord(FindExchangeBindStateReq findExchangeBindStateReq) {
        findExchangeBindStateReq=setFindExchangeBindReq(findExchangeBindStateReq);
        int i = tUserExchangeWalletMapper.queryAllRecordCount(findExchangeBindStateReq);
        List<TRocketBind> tRocketBinds = tUserExchangeWalletMapper.queryAllRecord(findExchangeBindStateReq);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("count",i);
        jsonObject.put("list",tRocketBinds);
        return ResponseResult.builder().result(jsonObject)
                .code(ErrorCode.REQ_SUCCESS.getCode())
                .msg(ErrorCode.REQ_SUCCESS.getDes()).build();
    }

    private FindExchangeBindStateReq setFindExchangeBindReq(FindExchangeBindStateReq findExchangeBindStateReq){
        if (findExchangeBindStateReq.getPageNum()!=null) {
            findExchangeBindStateReq.setPageNum((findExchangeBindStateReq.getPageNum() - 1) * 10);
            findExchangeBindStateReq.setPageNum2(10L);
        }else{
            findExchangeBindStateReq.setPageNum(0L);
            findExchangeBindStateReq.setPageNum2(10L);
        }
        return findExchangeBindStateReq;
    }
}
