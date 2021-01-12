package com.xinlian.common.exception;


import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.response.ResponseResult;

public class ReqParamException extends RuntimeException{

    public ResponseResult exceptionMsg(){
        ResponseResult result = new ResponseResult();
        result.responseResult(GlobalConstant.ResponseCode.PARAM_ERROR, "非法参数");
        return result;
    }
}
