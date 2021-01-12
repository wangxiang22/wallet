package com.xinlian.common.response;

import com.github.pagehelper.PageInfo;
import com.xinlian.common.result.ErrorInfoEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 返回体 - 分页需返回
 */
@Getter
@Setter
@AllArgsConstructor
public class ResponseResultPage<T> {
    private Integer code;
    private String status;
    private List<T> result;
    private String msg;
    private Long count;

    public ResponseResultPage(ErrorInfoEnum errorInfo) {
        this.code = errorInfo.getCode();
        this.msg = errorInfo.getMsg();
    }

    public ResponseResultPage(PageInfo pageInfo, String msg){
        this.code = ErrorInfoEnum.SUCCESS.getCode();
        this.msg = msg;
        this.count = pageInfo.getTotal();
        this.result = pageInfo.getList();
    }
    public ResponseResultPage(PageInfo pageInfo){
        this.code = ErrorInfoEnum.SUCCESS.getCode();
        this.count = pageInfo.getTotal();
        this.result = pageInfo.getList();
    }

    public ResponseResultPage(Boolean isFalse){
        if(isFalse){
            this.code=ErrorInfoEnum.SUCCESS.getCode();
            this.count=0L;
            this.result=new ArrayList<>();
        }
    }
    public ResponseResultPage(Boolean isFalse,String msg){
        if(isFalse){
            this.code=ErrorInfoEnum.SUCCESS.getCode();
            this.count=0L;
            this.result=new ArrayList<>();
        }
        this.msg = msg;
    }
}
