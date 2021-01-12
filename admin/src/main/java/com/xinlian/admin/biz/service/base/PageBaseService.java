package com.xinlian.admin.biz.service.base;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xinlian.common.Base.BaseCriteria;
import com.xinlian.common.Base.MapUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public abstract class PageBaseService<T> {

    private Class<T> entityClass;

    public PageBaseService() {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        entityClass = (Class) params[0];
    }
    public List<T> query(Map<String, Object> searchParams) throws Exception {
        return query(mapToModel(searchParams));
    }

    public PageInfo queryPage(Map<String, Object> searchParams) throws Exception {
        BaseCriteria criteria = BaseCriteria.newInstance();
        criteria.setPageParams(searchParams);
        PageHelper.startPage(criteria.getPageNum(), criteria.getPageSize());
        T t = mapToModel(searchParams);
        return new PageInfo(this.query(t));
    }

    public abstract List<T> query(T model) throws Exception;

    public PageInfo queryPage(T t)throws Exception{
        BaseCriteria criteria = BaseCriteria.newInstance();
        criteria.setPageParams(MapUtil.beanToMap(t));
        PageHelper.startPage(criteria.getPageNum(), criteria.getPageSize());
        return new PageInfo(this.query(t));
    }

    public T mapToModel(Map<String, Object> searchParams) throws Exception {
        return (T) MapUtil.mapToObject(searchParams, entityClass);
    }



}
