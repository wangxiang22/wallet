package com.xinlian.biz.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xinlian.biz.model.TVersion;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wjf
 * @since 2020-01-02
 */
@Repository
public interface TVersionMapper extends BaseMapper<TVersion> {

    TVersion queryOne();
}
