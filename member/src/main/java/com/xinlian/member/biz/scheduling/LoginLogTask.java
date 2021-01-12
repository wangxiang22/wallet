package com.xinlian.member.biz.scheduling;

import com.xinlian.biz.dao.TLoginLogMapper;
import com.xinlian.biz.model.TLoginLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class LoginLogTask {
    private static Logger logger = LoggerFactory.getLogger(LoginLogTask.class);

    @Autowired
    private TLoginLogMapper loginLogMapper;

    @Async
    public void addLoginLog(TLoginLog log){
        //保留 10条
        loginLogMapper.insert(log);
    }

}
