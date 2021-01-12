package com.xinlian.common.request;

public class IdReq extends PageReq{
    //会员 id
    private Long id;
    private Long uid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }
}
