package com.xinlian.biz.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xinlian.biz.model.TCountryDic;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface TCountryDicMapper extends BaseMapper<TCountryDic> {

    int insertBatch(List<TCountryDic> list);

    TCountryDic getModelByCode(@Param(value = "countryCode") int countryCode);
}
