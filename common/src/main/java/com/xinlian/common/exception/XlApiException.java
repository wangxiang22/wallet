package com.xinlian.common.exception;

import com.xinlian.common.enums.ErrorCode;

public class XlApiException extends RuntimeException {

    /**
     * 异常码
     */
    private ErrorCode errorCode;
    /**
     * 异常
     */
    private Throwable t;
    /**
     * 业务异常的消息体
     */
    private String busiExcpMesg;
    /**
     * 是否是业务异常
     */
    private boolean isBusiExcp;


    public XlApiException(ErrorCode errorCode, Throwable t){
        super(t);
        this.errorCode = errorCode;
        this.t = t;
    }

    /**
     * 业务异常
     * @param busiExcpMesg
     * @param t
     */
    public XlApiException(String busiExcpMesg, Throwable t) {
        super(busiExcpMesg, t);
        this.errorCode = ErrorCode.BUSI_EXCEPTION;
        this.busiExcpMesg = busiExcpMesg;
        this.t = t;
        this.isBusiExcp = true;
    }

    /**
     * 业务异常
     * @param busiExcpMesg
     */
    public XlApiException(String busiExcpMesg) {
        super(busiExcpMesg);
        this.errorCode = ErrorCode.BUSI_EXCEPTION;
        this.busiExcpMesg = busiExcpMesg;
        this.isBusiExcp = true;
    }

    public XlApiException(String busiExcpMesg, ErrorCode errorCode) {
        super(busiExcpMesg);
        this.errorCode = errorCode;
        this.busiExcpMesg = busiExcpMesg;
        this.isBusiExcp = true;
    }


    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Throwable getT() {
        return t;
    }

    public String getBusiExcpMesg() {
        return busiExcpMesg;
    }

    public boolean isBusiExcp() {
        return isBusiExcp;
    }


}
