package com.xinlian.common.dto;

import com.xinlian.biz.model.LotteryDraw;
import com.xinlian.biz.model.LotteryDrawPrizer;
import com.xinlian.biz.model.TWalletInfo;
import com.xinlian.biz.model.TWalletTradeOrder;
import lombok.Data;

@Data
public class DrawIntoDbDto {
    private LotteryDraw lotteryDraw;
    private LotteryDrawPrizer lotteryDrawPrizer;
    private TWalletInfo dbResult;
    private TWalletTradeOrder tWalletTradeOrder;
}
