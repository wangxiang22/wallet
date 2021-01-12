package com.xinlian.member.biz.springrunner;/*
package com.xinlian.wallet.biz.springrunner;

import com.xinlian.wallet.biz.test.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(100)
public class TestBugRunner implements ApplicationRunner {
    private final Logger logger = LoggerFactory.getLogger(TestBugRunner.class);

    @Autowired
    private TestService testService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("TestBugRunner.....................");
        //初始化
        testService.initCountryDic();
    }
}
*/
