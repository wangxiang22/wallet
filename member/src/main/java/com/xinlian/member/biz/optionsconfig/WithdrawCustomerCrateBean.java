package com.xinlian.member.biz.optionsconfig;

import com.xinlian.member.biz.redis.RedisConstant;
import com.xinlian.member.biz.service.WithdrawCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author Song
 * @date 2020-05-26 16:46
=======
import com.xinlian.member.biz.redis.RedisConstant;

/**
 * @author Song
 * @date 2020-05-29 14:27
>>>>>>> last_online_branch_20200511
 * @description
 */
@Component
@Order(1)
public class WithdrawCustomerCrateBean implements CommandLineRunner {

    @Autowired
    private WithdrawCustomerService withdrawCustomerService;

    @Override
    public void run(String... args) throws Exception {
        withdrawCustomerService.initWithdrawCustomerToCache(RedisConstant.WITHDRAW_CUSTOMER_UID);
    }
}

