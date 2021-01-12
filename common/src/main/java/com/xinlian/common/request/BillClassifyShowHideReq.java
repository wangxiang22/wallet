package com.xinlian.common.request;

import lombok.Data;

@Data
public class BillClassifyShowHideReq {
    /**
     * 账单分类id
     */
    private Long id;
    /**
     * 展示状态 - 0：不展示，1：展示
     */
    private Integer showState;
}
