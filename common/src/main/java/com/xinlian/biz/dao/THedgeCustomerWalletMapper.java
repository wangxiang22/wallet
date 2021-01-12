package com.xinlian.biz.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xinlian.biz.model.THedgeCustomerWallet;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 冻结客户资产记录表 Mapper 接口
 * </p>
 *
 * @author lt
 * @since 2020-05-29
 */
@Repository
public interface THedgeCustomerWalletMapper extends BaseMapper<THedgeCustomerWallet> {
    /**
     * 查询冻结状态的到期用户id列表
     * @return uidList
     */
    List<Long> findExpireFreezeUidList();
    /**
     * 修改已到期用户状态为解冻
     * @return
     */
    int updateFreezeStatus(List<Long> expireFreezeUidList);
}