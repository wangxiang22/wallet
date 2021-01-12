package com.xinlian.common.request;

import lombok.Data;

/**
 * @author lt
 */
@Data
public class PageNumSizeReq {
    private Integer pageNum = 1;
    private Integer pageSize = 10;

    public Integer getPageNum() {
        if (null == pageNum) {
            return 1;
        }
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum == null? 1 : pageNum;
    }

    public Integer getPageSize() {
        if (null == pageSize) {
            return 10;
        }
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize == null? 10 : pageSize;
    }
}
