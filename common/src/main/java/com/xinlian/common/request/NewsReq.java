package com.xinlian.common.request;

import lombok.Data;


@Data
public class NewsReq {

    private Long id;
    private Integer type;

    private Integer tid;
    private String title;
    private Long startTime;
    private Long endTime;

    private Long curPage;
    private Long pageSize;

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
