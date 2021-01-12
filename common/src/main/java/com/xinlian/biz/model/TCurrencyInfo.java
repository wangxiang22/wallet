package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import com.xinlian.common.response.CurrencyInfoRes;

import java.io.Serializable;

@TableName("t_currency_info")
public class TCurrencyInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 币种code
     */
    @TableField("currency_code")
    private String currencyCode;
    /**
     * 币种中文名称
     */
    @TableField("currency_name")
    private String currencyName;

    @TableField("img_url")
    private String imgUrl;
    /**
     * 币种状态 1 非可用 2 可用 
     */
    @TableField("status")
    private Integer status;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return "TCurrencyInfo{" +
        ", id=" + id +
        ", currencyCode=" + currencyCode +
        ", currencyName=" + currencyName +
        ", status=" + status +
        "}";
    }

    public CurrencyInfoRes currencyInfoRes(){
        CurrencyInfoRes res = new CurrencyInfoRes();
        res.setCurrencyCode(currencyCode);
        res.setCurrencyName(currencyName);
        res.setId(id);
        res.setThumb(imgUrl);
        return res;
    }
}
