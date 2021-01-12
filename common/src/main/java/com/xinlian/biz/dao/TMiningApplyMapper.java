package com.xinlian.biz.dao;

import com.xinlian.biz.model.TMiningApply;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xinlian.common.request.FindAllUserReq;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 挖矿申请表 Mapper 接口
 * </p>
 *
 * @author 无名氏
 * @since 2020-04-25
 */
@Repository
public interface TMiningApplyMapper extends BaseMapper<TMiningApply> {

    List<TMiningApply> findAllUser(FindAllUserReq findAllUserReq);

    Integer findAllUserCount(FindAllUserReq findAllUserReq);
}
