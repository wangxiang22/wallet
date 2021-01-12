package com.xinlian.common.request;

public class BalanceBillOperationPageReq extends PageNumSizeReq {
    /**
     * 币种名称
     */
    private String currencyName;
    /**
     * 开始对冲时间
     */
    private String startHedgeTime;
    /**
     * 结束对冲时间
     */
    private String endHedgeTime;

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getStartHedgeTime() {
        return startHedgeTime;
    }

    public void setStartHedgeTime(String startHedgeTime) {
        this.startHedgeTime = startHedgeTime;
    }

    public String getEndHedgeTime() {
        return endHedgeTime;
    }

    public void setEndHedgeTime(String endHedgeTime) {
        this.endHedgeTime = endHedgeTime;
    }

}
