package com.xinlian.common.request;

import lombok.Data;

@Data
public class VersionDataReq extends PageReq {

    private Integer id;

    private Integer vid;
    //类别，安卓还是苹果，1是安卓，2是苹果
    private Integer type;
}
