package com.xinlian.biz.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xinlian.biz.model.TChainOwner;
import com.xinlian.biz.model.TUploadChainOwnerRecord;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface TChainOwnerMapper extends BaseMapper<TChainOwner> {

    /**
     * 批量释放链权人相同值cat
     * @return
     */
    int batchDisposeSubtractLock(Map<String,Object> paramMap);

    /**
     * 批量
     * @param successUnionList
     */
    int batchInsertChainOwner(List<TUploadChainOwnerRecord> successUnionList);

    /**
     * 根据搜索条件查询链权人信息（分页）
     * @param tChainOwner
     * @return
     */
    List<TChainOwner> query(TChainOwner tChainOwner);
}
