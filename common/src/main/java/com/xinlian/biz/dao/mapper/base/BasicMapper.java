package com.xinlian.biz.dao.mapper.base;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * com.xinlian.biz.dao.mapper.base
 *
 * @author by Song
 * @date 2020/2/21 17:08
 */
public interface BasicMapper<T, PK> {

    T getById(@Param("id") PK id);

    T getByCriteria(T model);

    int queryCount(T model);

    List<T> query(T model);

    int insert(T t);

    int update(T t);

    int delete(PK id);

    void deleteByModel(T model);
}
