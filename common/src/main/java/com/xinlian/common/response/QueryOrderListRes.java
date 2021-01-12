package com.xinlian.common.response;

import com.xinlian.biz.model.TNewOrder;
import lombok.Data;

import java.util.List;

@Data
public class QueryOrderListRes {
    private List<TNewOrder> list ;
    private Integer count;
}
