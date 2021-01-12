package com.xinlian.member.server.controller.handler;

import com.xinlian.biz.dao.TOrderMapper;
import com.xinlian.biz.utils.AdminOptionsUtil;
import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
import com.xinlian.common.request.SellCatReq;
import com.xinlian.common.response.OrderOpenRes;
import com.xinlian.common.result.BizException;
import com.xinlian.common.utils.CommonUtil;
import com.xinlian.common.utils.UdunBigDecimalUtil;
import com.xinlian.member.biz.redis.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

import static com.xinlian.common.contants.OrderConstant.NOT_IN_SPOT_TIME;
import static com.xinlian.common.contants.OrderConstant.PRICE_OR_AMOUNT_OR_ADDR_NO;
import static com.xinlian.member.biz.redis.RedisConstant.ONCE_AMOUNT;
import static com.xinlian.member.biz.redis.RedisConstant.TODAY_AMOUNT;

/**
 * @author Song
 * @date 2020-08-07 09:53
 * @description Spot - handler class
 */
@Component
@Slf4j
public class SpotValidateHandler {

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private AdminOptionsUtil adminOptionsUtil;
    @Autowired
    private TOrderMapper orderMapper;


    public void sellCatCheckRequestParam(@NotNull SellCatReq sellCatReq){
        //check trade time
        notInSpotTime();
        //1.check req param
        if ( UdunBigDecimalUtil.judgeIfMinus(sellCatReq.getAmount())
                || UdunBigDecimalUtil.judgeIfMinus(sellCatReq.getPrice())
                || StringUtils.isEmpty(sellCatReq.getAddress())) {
            throw new BizException(PRICE_OR_AMOUNT_OR_ADDR_NO);
        }
        //check total1 is equals amount * price
        if(sellCatReq.getTotal().compareTo(UdunBigDecimalUtil.multiplyValueDecimal(sellCatReq.getAmount(),sellCatReq.getPrice())) != 0 ){
            throw new BizException(PRICE_OR_AMOUNT_OR_ADDR_NO);
        }
        //检查用户当次卖出量
        if (redisClient.get(ONCE_AMOUNT) != null) {
            if (sellCatReq.getAmount().compareTo(new BigDecimal(redisClient.get(ONCE_AMOUNT).toString())) == 1) {
                throw new BizException("单次兑出上限为" + redisClient.get(ONCE_AMOUNT) + "Cat");
            }
        }
        //检查该用户当日cat卖出总量
        checkTodayCat(sellCatReq);
    }

    private void checkTodayCat(SellCatReq sellCatReq) {
        if (redisClient.get(TODAY_AMOUNT) != null) {
            Long todayStartTime = CommonUtil.getTodayStartTime();
            Long todayEndTime = CommonUtil.getTodayEndTime();
            Long uid = sellCatReq.getUid();
            BigDecimal todayAmount = orderMapper.checkUserTodaySellAmount(todayStartTime, todayEndTime, uid);
            if (todayAmount != null) {
                if (todayAmount.add(sellCatReq.getAmount()).compareTo(new BigDecimal(redisClient.get(TODAY_AMOUNT).toString())) == 1) {
                    throw new BizException("单日兑出上限为" + redisClient.get(TODAY_AMOUNT) + "Cat");
                }
            }
        }
    }

    /**
     * 不在交易时间段内校验
     */
    public void notInSpotTime() {
        try {
            OrderOpenRes orderOpenRes = adminOptionsUtil.fieldEntityObject(
                    AdminOptionsBelongsSystemCodeEnum.ORDER_TIME_OUT.getBelongsSystemCode(), OrderOpenRes.class);
            boolean flag = CommonUtil.isTimeRange(orderOpenRes);
            if (!flag) {
                throw new BizException(NOT_IN_SPOT_TIME);
            }
        } catch (Exception e) {
            throw new BizException(NOT_IN_SPOT_TIME);
        }
    }
}
