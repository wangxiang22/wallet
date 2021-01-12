package com.xinlian.common.request;


import com.xinlian.common.exception.ReqParamException;

public interface ICheckParam {

    void checkParam();

    default void throwException(){
        throw new ReqParamException();
    }
}
