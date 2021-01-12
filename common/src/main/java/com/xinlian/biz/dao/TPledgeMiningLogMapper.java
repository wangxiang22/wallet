package com.xinlian.biz.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xinlian.biz.model.TPledgeMiningLog;
import com.xinlian.common.dto.PledgeUidStatusDto;
import com.xinlian.common.response.PledgeMiningRes;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 质押成功请求算力地球接口记录表 Mapper 接口
 * </p>
 *
 * @author lt
 * @since 2020-06-28
 */
@Repository
public interface TPledgeMiningLogMapper extends BaseMapper<TPledgeMiningLog> {
    /**
     * 获取质押成功的用户id（大于等于第一次上线时间）
     * @return uid默认升序，获取第一个
     */
    PledgeUidStatusDto findNotRequestOne(String time);

    /**
     * 获取质押成功的用户信息
     * @param uid 质押成功但未推送给算力地球的用户id
     * @return 组装的用户基本信息
     */
    PledgeMiningRes findPledgeUserOne(Long uid);
}
