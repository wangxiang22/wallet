package com.xinlian.biz.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xinlian.biz.model.hicat.TUser;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 *  HiCat-用户表Mapper 接口
 * </p>
 *
 * @author lt
 * @since 2020-07-15
 */
@Component
@DS("slave")
public interface TUserMapper extends BaseMapper<TUser> {
    /**
     * 查找HiCat用户信息
     * @param uidList 用户uid列表
     * @return 用户信息列表
     */
    List<TUser> findUserByUidList(List<String> uidList);
}
