package com.xinlian.common.exception;


import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.response.ResponseResult;

public class NoAccessException extends RuntimeException{

    public ResponseResult exceptionMsg(){
        ResponseResult result = new ResponseResult();
        result.responseResult(GlobalConstant.ResponseCode.NO_ACCESS, "无访问权限");
        return result;
    }
}
