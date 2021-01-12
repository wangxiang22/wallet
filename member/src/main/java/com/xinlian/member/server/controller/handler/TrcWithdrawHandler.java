package com.xinlian.member.server.controller.handler;

import com.xinlian.common.request.WithdrawCurrencyRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Song
 * @date 2020-08-19 20:40
 * @description trc withdraw
 */
@Component
@Slf4j
public class TrcWithdrawHandler {


    @Transactional
    public void trcWithdrawHandler(WithdrawCurrencyRequest withdrawCurrencyRequest){


    }
}
