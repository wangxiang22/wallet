package com.xinlian.common.request;

/**
 * <p>
 *  分页基类
 * </p>
 *
 * @author cms
 * @since 2020-04-08
 */
public abstract class BaseReq {

    private int page = 1;

    private int pageSize = 10;

    private int start;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        this.start = (page - 1) * pageSize;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
        this.start = (page - 1) * pageSize;
    }
}
