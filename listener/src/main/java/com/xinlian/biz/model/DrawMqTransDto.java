package com.xinlian.biz.model;

import lombok.Data;

@Data
public class DrawMqTransDto {
    private LotteryDrawReq lotteryDrawReq;
    private Long uid;
    private LotteryDraw lotteryDraw;
}
