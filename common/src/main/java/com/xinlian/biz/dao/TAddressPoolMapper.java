package com.xinlian.biz.dao;

import com.xinlian.biz.model.TAddressPool;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.springframework.stereotype.Component;

/**
 * @author wjf
 * @since 2019-12-21
 */
@Component
public interface TAddressPoolMapper extends BaseMapper<TAddressPool> {

    /**
     * 更新model
     * @return
     */
    int updateModel(TAddressPool addressPool);

    /**
     * 根据对象获取符合条件的一条记录
     * @param addressPool
     * @return
     */
    TAddressPool getByCriteria(TAddressPool addressPool);

    /**
     * 往地址池加入地址
     * @param addressPool
     * @return
     */
    int addAddressTools(TAddressPool addressPool);

    /**
     * 地址池多少数量
     */
    int getBatchCount();
}
