package com.xinlian.biz.model;

import java.io.Serializable;

public class TCurrencyTradeWalletRef implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * currency_info表id
     */
    private Long currencyId;
    private String mainCurrencyCode;
    private String childCurrencyCode;
    /**
     * 币中简称
     */
    private String currencyName;
    /**
     * 币种英文名称
     */
    private String currencyEnglishName;
    /**
     * 币种中文名称
     */
    private String currencyChineseName;
    /**
     * 对接接口系统code
     */
    private String belongToSystemCode;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public String getMainCurrencyCode() {
        return mainCurrencyCode;
    }

    public void setMainCurrencyCode(String mainCurrencyCode) {
        this.mainCurrencyCode = mainCurrencyCode;
    }

    public String getChildCurrencyCode() {
        return childCurrencyCode;
    }

    public void setChildCurrencyCode(String childCurrencyCode) {
        this.childCurrencyCode = childCurrencyCode;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getCurrencyEnglishName() {
        return currencyEnglishName;
    }

    public void setCurrencyEnglishName(String currencyEnglishName) {
        this.currencyEnglishName = currencyEnglishName;
    }

    public String getCurrencyChineseName() {
        return currencyChineseName;
    }

    public void setCurrencyChineseName(String currencyChineseName) {
        this.currencyChineseName = currencyChineseName;
    }

    public String getBelongToSystemCode() {
        return belongToSystemCode;
    }

    public void setBelongToSystemCode(String belongToSystemCode) {
        this.belongToSystemCode = belongToSystemCode;
    }

    @Override
    public String toString() {
        return "TCurrencyTradeWalletRef{" +
        ", id=" + id +
        ", currencyId=" + currencyId +
        ", mainCurrencyCode=" + mainCurrencyCode +
        ", childCurrencyCode=" + childCurrencyCode +
        ", currencyName=" + currencyName +
        ", currencyEnglishName=" + currencyEnglishName +
        ", currencyChineseName=" + currencyChineseName +
        ", belongToSystemCode=" + belongToSystemCode +
        "}";
    }
}
