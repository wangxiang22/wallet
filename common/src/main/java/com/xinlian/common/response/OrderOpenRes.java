package com.xinlian.common.response;

import lombok.Data;

@Data
public class OrderOpenRes {
    private String startAm;
    private String endAm;
    private String startPm;
    private String endPm;
    private String isOpen;
    private String orderTimeOut;
}
