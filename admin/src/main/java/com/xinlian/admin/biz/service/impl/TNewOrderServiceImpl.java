package com.xinlian.admin.biz.service.impl;


import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.xinlian.admin.biz.service.TNewOrderService;
import com.xinlian.admin.biz.template.TNewOrderTemplate;
import com.xinlian.biz.dao.TNewOrderMapper;
import com.xinlian.biz.model.TNewOrder;
import com.xinlian.common.dto.TNewOrderDto;
import com.xinlian.common.express.ExpressConfig;
import com.xinlian.common.request.OrderExportReq;
import com.xinlian.common.request.OrderSendReq;
import com.xinlian.common.request.QueryOrderListReq;
import com.xinlian.common.request.UpdateNewOrderReq;
import com.xinlian.common.response.QueryOrderListRes;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


@Slf4j
@Service
public class TNewOrderServiceImpl extends ServiceImpl<TNewOrderMapper, TNewOrder> implements TNewOrderService {

    @Autowired
    private TNewOrderMapper tNewOrderMapper;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ExpressConfig expressConfig;
    @Autowired
    private TNewOrderTemplate tNewOrderTemplate;


    @Override
    public QueryOrderListRes queryOrderList(QueryOrderListReq queryOrderListReq) {
        //根据条件搜索订单列表
        List<TNewOrder> list = tNewOrderMapper.queryOrderList(queryOrderListReq);
        //根据条件查询订单条数
        Integer count = tNewOrderMapper.queryOrderCount(queryOrderListReq);
        //构建响应实体
        QueryOrderListRes queryOrderListRes = new QueryOrderListRes();
        queryOrderListRes.setCount(count);
        queryOrderListRes.setList(list);
        return queryOrderListRes;

    }

    @Override
    @Transactional
    public void exportOrders(OrderExportReq orderExportReq, HttpServletRequest request, HttpServletResponse response) {
        //输出流
        ServletOutputStream out = null;
        //excel写入流
        ExcelWriter writer = null;
        //根据条件搜索要导出的list
        List<TNewOrder> list = tNewOrderMapper.exportOrders(orderExportReq);
        //创建要导出的list对象
        List<TNewOrderDto> exportList = new ArrayList<>();
        //将数据库里的数据放到要导出的list中
        for (TNewOrder tNewOrder : list) {
            TNewOrderDto tNewOrderDto = new TNewOrderDto();
            BeanUtils.copyProperties(tNewOrder,tNewOrderDto);
            exportList.add(tNewOrderDto);
        }
        if (list.size()==0){
            throw new BizException("暂无数据");
        }
        try {
            int i = tNewOrderMapper.updateByIds(list);
            if (i==0){
                throw new BizException("导出异常");
            }
            //获取输出流
            out = response.getOutputStream();
            //构建写入对象
            writer = new ExcelWriter(out, ExcelTypeEnum.XLS,true);
            //文件名
            String fileName = "未发货订单";
            //设置excel的sheet
            Sheet sheet1 = new Sheet(1, 0, TNewOrderDto.class);
            sheet1.setSheetName("未发货订单");
            //将要导出的list写入输入流
            writer.write0(exportList, sheet1);
            //设置响应字符集
            response.setCharacterEncoding("utf-8");
//            response.setContentType("multipart/form-data");
            //设置响应类型
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            //设置响应头
            response.setHeader("Content-disposition", "attachment;filename="
                    + new String((fileName + ".xls").getBytes(), "ISO8859-1"));
            out.flush();
        } catch (IOException e) {
            throw new BizException("导出发生异常");
        } finally {
            //关流
            if (Objects.nonNull(writer)) {
                writer.finish();
            }
            try {
                if (Objects.nonNull(out)) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void deliver(OrderSendReq orderExportReq) {
        TNewOrder tNewOrder = tNewOrderMapper.selectById(orderExportReq.getOrderId());
        if(tNewOrder == null){
            throw new BizException("订单不存在");
        }
        if(tNewOrder.getStatus() != 0){
            throw new BizException("订单状态有误");
        }
        tNewOrder.setExpressCode(orderExportReq.getExpressCode());
        tNewOrder.setExpressComId(orderExportReq.getExpressCompanyComId());
        tNewOrder.setExpressName(orderExportReq.getExpressCompanyName());
        tNewOrder.setSendTime(new Date());
        tNewOrder.setStatus(1);
        int result = tNewOrderMapper.updateById(tNewOrder);
        if(result == 0){
            throw new BizException("发货失败");
        }
        //极光推送
        tNewOrderTemplate.publishMessage(tNewOrder);
    }

    @Override
    public JSONObject queryExpress(Long orderId) {
        TNewOrder tNewOrder = tNewOrderMapper.selectById(orderId);
        if(tNewOrder == null){
            throw new BizException("订单不存在");
        }
        return getExpressData(tNewOrder.getExpressComId(), tNewOrder.getExpressCode());
    }

    @Override
    public void updateNewOrderById(UpdateNewOrderReq req) {
        int resultCount = tNewOrderMapper.updateNewOrderById(req);
        if(resultCount == 0){
            throw new BizException("修改订单信息失败");
        }
    }

    private JSONObject getExpressData(String expressId, String expressNo) {
        // 添加header
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "APPCODE ".concat(expressConfig.getAppCode()));
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, headers);
        // 添加参数
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(expressConfig.getQueryExpressUrl());
        builder.queryParam("comid", expressId);
        builder.queryParam("number", expressNo);
        final ResponseEntity<String> responseEntity = restTemplate
                .exchange(builder.build().toString(), HttpMethod.GET, request, String.class);
        JSONObject jsonObject = JSONObject.parseObject(responseEntity.getBody());
        if (jsonObject.containsKey("data")) {
            return jsonObject.getJSONObject("data");
        } else {
            return null;
        }
    }

}
