package com.xinlian.member.biz.service;

import com.xinlian.biz.model.LotteryDrawPrizer;
import com.baomidou.mybatisplus.service.IService;
import com.xinlian.common.response.ResponseResult;

/**
 * <p>
 * 中奖者表 服务类
 * </p>
 *
 * @author lx
 * @since 2020-06-05
 */
public interface LotteryDrawPrizerService extends IService<LotteryDrawPrizer> {

    ResponseResult runningHorseLight();

    ResponseResult insert10();
}
