package com.xinlian.biz.dao;

import com.xinlian.biz.model.LotteryDraw;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lx
 * @since 2020-06-05
 */
@Repository
public interface LotteryDrawMapper extends BaseMapper<LotteryDraw> {

    void subStock(Integer id);
}
