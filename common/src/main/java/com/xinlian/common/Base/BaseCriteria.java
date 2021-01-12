package com.xinlian.common.Base;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public abstract class BaseCriteria<T> {
    /**
     * @Fields pageNo : 页码
     */
    private Integer pageNum;

    /**
     * @Fields pageSize : 页数
     */
    private Integer pageSize;

    public void setPageParams(Map<String, Object> searchParams) {
        setPageNum(searchParams.get(Constant.PAGENUM_NAME)==null? searchParams.get("pageNum")==null? Constant.DEFAULT_PAGE_NO:
                Integer.parseInt(searchParams.get("pageNum").toString()):
                Integer.parseInt(searchParams.get(Constant.PAGENUM_NAME).toString()));
        setPageSize(
                searchParams.get(Constant.PAGESIZE_NAME)==null? searchParams.get("pageSize")==null? Constant.DEFAULT_PAGE_SIZE:
                        Integer.parseInt( searchParams.get("pageSize").toString()):
                        Integer.parseInt(searchParams.get(Constant.PAGESIZE_NAME).toString()));
    }

    static class InnerCriteria<T> extends BaseCriteria<T> {
    }

    public static BaseCriteria newInstance() {
        return new InnerCriteria();
    }
}
