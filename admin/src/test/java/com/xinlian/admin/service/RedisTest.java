package com.xinlian.admin.service;

import com.xinlian.admin.biz.redis.RedisClient;
import com.xinlian.admin.service.base.BaseServiceTest;
import com.xinlian.biz.utils.AdminOptionsUtil;
import com.xinlian.common.dto.LotteryDrawDto;
import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.xinlian.admin.biz.redis.RedisConstant.REDIS_KEY_SESSION_ADMIN_OPTION;
import static com.xinlian.common.redis.RedisConstant.LOTTERY_DRAW;

public class RedisTest extends BaseServiceTest {
    @Autowired
    private RedisClient redisClient;
    @Autowired
    AdminOptionsUtil adminOptionsUtil;
    @Test
    public void a() throws Exception {
        redisClient.deleteByKey(REDIS_KEY_SESSION_ADMIN_OPTION.concat(LOTTERY_DRAW));
        System.out.println(adminOptionsUtil.fieldEntityObject(AdminOptionsBelongsSystemCodeEnum.LOTTERY_DRAW.getBelongsSystemCode(), LotteryDrawDto.class));
    }
}
