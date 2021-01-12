package com.xinlian.biz.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xinlian.biz.model.MiddleModel;
import com.xinlian.biz.model.TReleaseCatRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 释放cat记录表 Mapper 接口
 * </p>
 *
 * @author 插件生成
 * @since 2020-01-13
 */
@Component
public interface TReleaseCatRecordMapper extends BaseMapper<TReleaseCatRecord> {

    /**
     * 批量插入
     */
    void batchInsert(List<TReleaseCatRecord> list);

    /**
     * 获取未处理的集合
     * @return
     */
    List<TReleaseCatRecord> getWaitReleaseCatRecord(BigDecimal groupByReleaseCatNum);

    List<BigDecimal> groupByReleaseCatNum();

    //根据旧状态修改成新状态
    int batchUpdateToProcessing(@Param(value = "releaseCatRecordList") List<TReleaseCatRecord> releaseCatRecordList,
                                @Param(value = "oldStatus") int oldStatus,
                                @Param(value = "newStatus") int newStatus);

    /**
     * 获取等待处理集合
     * @return
     */
    MiddleModel getWaitDisposeMiddleSet();

    /**
     * 更新
     */
    int updateMiddleStatus(@Param(value = "orderId")Long orderId,
                       @Param(value = "oldStatus") int oldStatus,
                       @Param(value = "newStatus") int newStatus);

    /**
     * 删除交易记录
     * @param orderId
     * @return
     */
    int deleteByKeyOrderId(@Param(value = "orderId") Long orderId);

    /**
     * 更新钱包某个币种余额
     * @param uid
     * @param minusDecimalValue
     * @return
     */
    int updateWalletInfoBalanceNum(@Param(value = "uid")Long uid,
                         @Param(value = "minusDecimalValue")BigDecimal minusDecimalValue);

    int checkLockedPositionIsUId(@Param(value = "uid")Long uid);
}

