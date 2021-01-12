package com.xinlian.common.request;

public class PageReq {
    private Long curPage;
    private Long pageSize;
    private Integer type;


    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getCurPage() {
        return curPage;
    }

    public void setCurPage(Long curPage) {
        this.curPage = curPage;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }



    public long pickUpCurPage(){
        if(curPage==null || curPage.longValue()<=0){
            return 1;
        }
       return curPage.longValue();
    }

    public long pickUpPageSize(){
        if(pageSize==null || pageSize.longValue()<=0){
            return 10;
        }
        if(pageSize.longValue() > 5000){
            return 5000;
        }
        return pageSize.longValue();
    }

    public long pickUpOffset(){
        return (pickUpCurPage()-1)*pickUpPageSize();
    }
}
