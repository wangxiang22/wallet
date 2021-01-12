package com.xinlian.admin.biz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xinlian.admin.biz.service.TOrderInfoService;
import com.xinlian.biz.dao.TOrderInfoMapper;
import com.xinlian.biz.dao.TOrderMapper;
import com.xinlian.biz.dao.TUserInfoMapper;
import com.xinlian.biz.model.TOrder;
import com.xinlian.biz.model.TOrderInfo;
import com.xinlian.common.dto.OrderInfoRealTimeDto;
import com.xinlian.common.dto.OrderInfoTotalAmountDto;
import com.xinlian.common.request.OrderInfoRealTimePageReq;
import com.xinlian.common.request.OrderInfoTotalAmountReq;
import com.xinlian.common.request.QuerySpotInfoReq;
import com.xinlian.common.response.*;
import com.xinlian.common.result.ErrorInfoEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
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
        querySpotInfo.setStart((querySpotInfo.getPage() - 1) * querySpotInfo.getPageSize());
        List<TOrderInfo> list = tOrderInfoMapper.querySpotInfo(querySpotInfo);
        int count = tOrderInfoMapper.querySpotInfoCount(querySpotInfo);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("list",list);
        jsonObject.put("count",count);
        return ResponseResult.ok(jsonObject);
    }

    @Override
    public ResponseResult exportSpotInfo(QuerySpotInfoReq querySpotInfo,HttpServletResponse httpServletResponse) {
//        httpServletResponse.setContentType("application/vnd.ms-excel;charset=utf-8");
//        httpServletResponse.setHeader("Access-Control-Expose-Headers", "Content-disposition");
//        httpServletResponse.setHeader("Content-disposition", "attachment;filename=" + "交易明细" + System.currentTimeMillis() + ".xls");
//        String title, Map<String,String> headMap, JSONArray ja, HttpServletResponse response
//        Map<String,String> headMap = new LinkedHashMap<String,String>();
//        headMap.put("id","id");
//        headMap.put("username","用户名");
//        headMap.put("amount","数量/cat");
//        headMap.put("price","cat单价/usdt");
//        headMap.put("total","总额/usdt");
//        headMap.put("orderId","订单号");
//        headMap.put("cat","cat余额");
//        headMap.put("usdt","usdt余额");
//        headMap.put("addr","地址");
//        headMap.put("tradeType","交易方向0买1卖");
//        headMap.put("uid","uid");
        List<TOrderInfo> list = tOrderInfoMapper.exportSpotInfo(querySpotInfo);
//        JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(list));
//        ExcelUtil.downloadExcelFile("交易明细",headMap,jsonArray,httpServletResponse);
        return ResponseResult.ok(list);
    }

    @Override
    public ResponseResult exportQueryEveryOrderList(QuerySpotInfoReq querySpotInfo) {
        List<TOrder> list = tOrderMapper.exportQueryEveryOrderList(querySpotInfo);
        return ResponseResult.ok(list);
    }

    @Override
    public ResponseResult queryEveryOrderList(QuerySpotInfoReq querySpotInfo) {
        querySpotInfo.setStart((querySpotInfo.getPage() - 1) * querySpotInfo.getPageSize());//todo start这块怎么他妈的怎么不继承父类的值了
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

    @Override
    public PageResult<List<OrderInfoRealTimeRes>> findRealTimeOrderInfoPage(OrderInfoRealTimePageReq req) {
        PageResult<List<OrderInfoRealTimeRes>> result = new PageResult<>();
        result.setCode(ErrorInfoEnum.SUCCESS.getCode());
        PageHelper.startPage(req.getPageNum(),req.getPageSize());
        List<OrderInfoRealTimeDto> realTimeDtoList = tOrderInfoMapper.findRealTimeOrderInfoPage(req);
        List<OrderInfoRealTimeRes> realTimeResList = new ArrayList<>();
        if (null != realTimeDtoList && realTimeDtoList.size() > 0) {
            realTimeDtoList.forEach(OrderInfoRealTimeDto -> realTimeResList.add(OrderInfoRealTimeDto.orderInfoRealTimeRes()));
            PageInfo<OrderInfoRealTimeDto> pageInfo = new PageInfo<>(realTimeDtoList);
            result.setCurPage(pageInfo.getPageNum());
            result.setPageSize(pageInfo.getPageSize());
            result.setTotal(pageInfo.getTotal());
            result.setResult(realTimeResList);
        }
        return result;
    }

    @Override
    public ResponseResult<List<OrderInfoRealTimeRes>> findRealTimeOrderInfo(OrderInfoRealTimePageReq req) {
        ResponseResult<List<OrderInfoRealTimeRes>> result = new ResponseResult<>();
        result.setCode(ErrorInfoEnum.SUCCESS.getCode());
        List<OrderInfoRealTimeDto> realTimeDtoList = tOrderInfoMapper.findRealTimeOrderInfoPage(req);
        List<OrderInfoRealTimeRes> realTimeResList = new ArrayList<>();
        if (null != realTimeDtoList && realTimeDtoList.size() > 0) {
            realTimeDtoList.forEach(OrderInfoRealTimeDto -> realTimeResList.add(OrderInfoRealTimeDto.orderInfoRealTimeRes()));
            result.setResult(realTimeResList);
        }
        return result;
    }

    @Override
    public ResponseResult<OrderInfoTotalAmountRes> findSellerBuyerAltogether(OrderInfoTotalAmountReq req) {
        ResponseResult<OrderInfoTotalAmountRes> result = new ResponseResult<>();
        result.setCode(ErrorInfoEnum.SUCCESS.getCode());
        OrderInfoTotalAmountDto sellerBuyerAltogether = tOrderInfoMapper.findSellerBuyerAltogether(req);
        if (null == sellerBuyerAltogether) {
            OrderInfoTotalAmountRes orderInfoTotalAmountResZero = new OrderInfoTotalAmountRes();
            orderInfoTotalAmountResZero.setSellerAltogetherOutAmount("-0.0000");
            orderInfoTotalAmountResZero.setSellerAltogetherInTotal("+0.0000");
            orderInfoTotalAmountResZero.setBuyerAltogetherOutTotal("-0.0000");
            orderInfoTotalAmountResZero.setBuyerAltogetherInAmount("+0.0000");
            orderInfoTotalAmountResZero.setCatMargin("0.0000");
            orderInfoTotalAmountResZero.setUsdtMargin("0.0000");
            result.setResult(orderInfoTotalAmountResZero);
        }else {
            result.setResult(sellerBuyerAltogether.orderInfoTotalAmountRes());
        }
        return result;
    }


}
