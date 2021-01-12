package com.xinlian.biz.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xinlian.biz.model.TrcUsdtAddressPool;
import org.springframework.stereotype.Component;

/**
 * @author wjf
 * @since 2019-12-21
 */
@Component
public interface TrcUsdtAddressPoolMapper extends BaseMapper<TrcUsdtAddressPool> {

    /**
     * 更新model
     * @return
     */
    int updateModel(TrcUsdtAddressPool trcUsdtAddressPool);

    /**
     * 根据对象获取符合条件的一条记录
     * @param trcUsdtAddressPool
     * @return
     */
    TrcUsdtAddressPool getByCriteria(TrcUsdtAddressPool trcUsdtAddressPool);

    /**
     * 往地址池加入地址
     * @param trcUsdtAddressPool
     * @return
     */
    int addTrcAddressTools(TrcUsdtAddressPool trcUsdtAddressPool);


}
