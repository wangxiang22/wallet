package com.xinlian.biz.dao;

import com.xinlian.biz.model.LotteryDrawPrizer;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 中奖者表 Mapper 接口
 * </p>
 *
 * @author lx
 * @since 2020-06-05
 */
@Repository
public interface LotteryDrawPrizerMapper extends BaseMapper<LotteryDrawPrizer> {

    List<LotteryDrawPrizer> runningHorseLight();

    List<LotteryDrawPrizer> queryPrize123();

    List<LotteryDrawPrizer> queryPrize4();

    List<LotteryDrawPrizer> queryPrizeRandom8();
}
