package com.xinlian.member.biz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.xinlian.biz.dao.TOrderInfoMapper;
import com.xinlian.biz.dao.TOrderMapper;
import com.xinlian.biz.dao.TUserInfoMapper;
import com.xinlian.biz.model.TOrder;
import com.xinlian.biz.model.TOrderInfo;
import com.xinlian.common.request.QuerySpotInfoReq;
import com.xinlian.common.response.QueryBuyerRes;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.service.TOrderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lx
 * @since 2020-06-19
 */
@Service
public class TOrderInfoServiceImpl extends ServiceImpl<TOrderInfoMapper, TOrderInfo> implements TOrderInfoService {
    @Autowired
    private TOrderInfoMapper tOrderInfoMapper;
    @Autowired
    private TOrderMapper tOrderMapper;
    @Autowired
    private TUserInfoMapper tUserInfoMapper;

    @Override
    public ResponseResult querySpotInfo(QuerySpotInfoReq querySpotInfo) {
        List<TOrderInfo> list = tOrderInfoMapper.querySpotInfo(querySpotInfo);
        int count = tOrderInfoMapper.querySpotInfoCount(querySpotInfo);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("list",list);
        jsonObject.put("count",count);
        return ResponseResult.ok(jsonObject);
    }

    @Override
    public ResponseResult queryEveryOrderList(QuerySpotInfoReq querySpotInfo) {
        List<TOrder> list = tOrderMapper.queryEveryOrderList(querySpotInfo);
        Integer count = tOrderMapper.queryEveryOrderListCount(querySpotInfo);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("list",list);
        jsonObject.put("count",count);
        return ResponseResult.ok(jsonObject);
    }

    @Override
    public ResponseResult queryBuyerInfoByAddr(String addr) {
        QueryBuyerRes queryBuyerRes = tUserInfoMapper.queryBuyerInfoByAddr(addr);
        return ResponseResult.ok(queryBuyerRes);
    }


}
