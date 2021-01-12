package com.xinlian.biz.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xinlian.biz.model.TUploadChainOwnerRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 上传链权人信息记录表 Mapper 接口
 * </p>
 *
 * @author 代码生成
 * @since 2020-01-14
 */
@Component
public interface TUploadChainOwnerRecordMapper extends BaseMapper<TUploadChainOwnerRecord> {

    int batchInsert(List<TUploadChainOwnerRecord> list);

    TUploadChainOwnerRecord getWaitDisposeInfo();

    List<TUploadChainOwnerRecord> getCoincidentChainOwner(@Param(value = "getLimit") Integer getLimit);

    int batchUpdate(List<TUploadChainOwnerRecord> list,
                    @Param(value = "oldStatus") int oldStatus,
                    @Param(value = "newStatus") int newStatus);

    /**
     * 获取已存在的链权人集合
     * @return
     */
    List<TUploadChainOwnerRecord> getExistChainOwners();
}
