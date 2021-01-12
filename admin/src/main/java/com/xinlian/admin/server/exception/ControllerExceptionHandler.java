package com.xinlian.admin.server.exception;

import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.exception.NoAccessException;
import com.xinlian.common.exception.ReLoginException;
import com.xinlian.common.exception.ReqParamException;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import com.xinlian.common.result.ErrorInfoEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@ControllerAdvice
public class ControllerExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);


    @ExceptionHandler(ReLoginException.class)
    @ResponseBody
    public ResponseResult reLoginException(ReLoginException e) {
        return e.exceptionMsg();
    }

    @ExceptionHandler(NoAccessException.class)
    @ResponseBody
    public ResponseResult noAccessException(NoAccessException e){
        return  e.exceptionMsg();
    }

    @ExceptionHandler(ReqParamException.class)
    @ResponseBody
    public ResponseResult reqParamException(ReqParamException e) {
        return e.exceptionMsg();
    }

    @ExceptionHandler(BizException.class)
    @ResponseBody
    public ResponseResult BizException(BizException e) {
        return ResponseResult.builder().msg(e.getMsg()).code(e.getCode()).build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseResult allException(Exception e) {
        logger.error(e.getMessage(), e);
        ResponseResult result = new ResponseResult();
        result.responseResult(GlobalConstant.ResponseCode.SYS_ERROR, "系统繁忙");
        return result;
    }

    @ExceptionHandler(BindException.class)
    public ResponseResult errorHandlerAdam(HttpServletRequest request,
                                       BindException exception) {
        logger.info("======Exception_request:"+request.getRequestURL());
        logger.info("======Exception_message:"+exception.getMessage());
        String errMsg = buildParamsErrorResponse(exception);
        return new ResponseResult(ErrorInfoEnum.FAILED.getCode(),errMsg);
    }

    private String buildParamsErrorResponse(BindException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<ObjectError> errors = bindingResult.getAllErrors();
        StringBuilder sb = new StringBuilder("参数校验错误:");
        for (ObjectError error : errors) {
            sb.append(error.getDefaultMessage());
            sb.append(";");
        }
        String errMsg = sb.toString();
        if(errMsg.length() > 63){
            errMsg = errMsg.substring(0,63);
        }
        return errMsg;
    }
}
