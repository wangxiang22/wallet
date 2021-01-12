package com.xinlian.common.request;

import lombok.Data;

@Data
public class OrderListReq {
    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页的数量
     */
    private Integer pageNum = 10;

}
