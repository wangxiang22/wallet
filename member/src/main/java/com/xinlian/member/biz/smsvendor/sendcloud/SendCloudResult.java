package com.xinlian.member.biz.smsvendor.sendcloud;

import java.util.List;

/**
 * @author Song
 * @date 2020-07-13 11:26
 * @description
 */
public class SendCloudResult {

    /**
     * result : true
     * statusCode : 200
     * message : 请求成功
     * info : {"successCount":1,"smsIds":["1594605408496_144785_15397_746110_cgkhrt$15801525748"]}
     */

    private boolean result;
    private int statusCode;
    private String message;
    private InfoBean info;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public InfoBean getInfo() {
        return info;
    }

    public void setInfo(InfoBean info) {
        this.info = info;
    }

    static class InfoBean {
        /**
         * successCount : 1
         * smsIds : ["1594605408496_144785_15397_746110_cgkhrt$15801525748"]
         */

        private int successCount;
        private List<String> smsIds;

        public int getSuccessCount() {
            return successCount;
        }

        public void setSuccessCount(int successCount) {
            this.successCount = successCount;
        }

        public List<String> getSmsIds() {
            return smsIds;
        }

        public void setSmsIds(List<String> smsIds) {
            this.smsIds = smsIds;
        }
    }
}
