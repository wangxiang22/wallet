package com.xinlian.admin.service;

import com.xinlian.admin.biz.service.UserAuthManagerService;
import com.xinlian.admin.service.base.BaseServiceTest;
import com.xinlian.common.request.UserAuthQueryReq;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Song
 * @date 2020-08-05 11:22
 * @description
 */
public class UserAuthManagerServiceTest extends BaseServiceTest {

    @Autowired
    private UserAuthManagerService userAuthManagerService;

    @Test
    public void testQueryAll(){
        UserAuthQueryReq userAuthQueryReq = new UserAuthQueryReq();
        userAuthQueryReq.setPageNum(1L);
        try {
            userAuthManagerService.queryAll(userAuthQueryReq);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
