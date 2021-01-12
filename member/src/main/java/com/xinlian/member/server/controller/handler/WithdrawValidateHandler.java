package com.xinlian.member.server.controller.handler;

import com.xinlian.common.request.WithdrawCurrencyRequest;
import com.xinlian.common.result.BizException;
import com.xinlian.common.utils.UdunBigDecimalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

/**
 * @author Song
 * @date 2020-07-31 09:32
 * @description 检验提币接口参数
 */
@Slf4j
@Component
public class WithdrawValidateHandler {

    /**
     * validate param by withdraw
     * @param withdrawCurrencyRequest withdraw param request
     */
    public void validateParameter(@NotNull WithdrawCurrencyRequest withdrawCurrencyRequest) {
        if (StringUtils.isBlank(withdrawCurrencyRequest.getCoin_id())) {
            throw new BizException("参数异常，请核实!");
        }
        if (StringUtils.isBlank(withdrawCurrencyRequest.getAddress())) {
            throw new BizException("请输入正确地址!");
        }
        //is validate frame work - to request exist common.jar
        if (!withdrawCurrencyRequest.getAddress().substring(0, 2).equals("0x")
                && withdrawCurrencyRequest.getAddress().length() != 42) {
            throw new BizException("请输入正确地址!");
        }
        //check提交资产是否为正值
        if(UdunBigDecimalUtil.judgeIfMinus(withdrawCurrencyRequest.getNum())){
            throw new BizException("请输入正确的交易金额！");
        }
    }
}
