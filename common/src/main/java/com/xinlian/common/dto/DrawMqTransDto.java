package com.xinlian.common.dto;

import com.xinlian.biz.model.LotteryDraw;
import com.xinlian.common.request.LotteryDrawReq;
import lombok.Data;

@Data
public class DrawMqTransDto {
    private LotteryDrawReq lotteryDrawReq;
    private Long uid;
    private LotteryDraw lotteryDraw;
}
