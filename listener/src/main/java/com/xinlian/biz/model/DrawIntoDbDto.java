package com.xinlian.biz.model;

import lombok.Data;

@Data
public class DrawIntoDbDto {
    private LotteryDraw lotteryDraw;
    private LotteryDrawPrizer lotteryDrawPrizer;
    private TWalletInfo dbResult;
    private TWalletTradeOrder tWalletTradeOrder;
}
