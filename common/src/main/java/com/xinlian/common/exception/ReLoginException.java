package com.xinlian.common.exception;


import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.response.ResponseResult;

public class ReLoginException extends RuntimeException{

    public ResponseResult exceptionMsg(){
        ResponseResult result = new ResponseResult();
        result.responseResult(GlobalConstant.ResponseCode.RE_LOGIN, "请重新登录");
        return result;
    }

}
