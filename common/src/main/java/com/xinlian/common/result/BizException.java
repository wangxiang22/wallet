package com.xinlian.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 应用统一错误异常
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BizException extends RuntimeException {

    private Integer code;
    private String msg;

    public BizException(ErrorInterface errorInfo) {
        this.msg = errorInfo.getMsg();
        this.code = errorInfo.getCode();
    }

    public BizException(ErrorInterface errorInfo, String errorMsg) {
        this.msg = errorInfo.getMsg()+errorMsg;
        this.code = errorInfo.getCode();
    }

    public BizException(String errMsg) {
        this.code = ErrorInfoEnum.PARAM_ERR.getCode();
        this.msg = errMsg;
    }

    public BizException(ErrorInterface errorInterface,String replaceMsg,boolean isReplace) {
        String errorMsg = errorInterface.getMsg();
        if (isReplace){
            errorMsg = errorMsg.replaceAll("@@", replaceMsg);
        }
        this.msg = errorMsg;
        this.code = errorInterface.getCode();
    }



}
