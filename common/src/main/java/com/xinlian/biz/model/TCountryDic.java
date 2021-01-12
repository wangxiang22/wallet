package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import com.xinlian.common.response.CountryDicRes;

import java.io.Serializable;

@TableName("t_country_dic")
public class TCountryDic implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 英文名称
     */
    @TableField("en")
    private String en;
    /**
     * 中文名称
     */
    @TableField("zh")
    private String zh;
    /**
     * 首字母缩写
     */
    @TableField("locale")
    private String locale;
    /**
     * 编码
     */
    @TableField("code")
    private Integer code;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public String getZh() {
        return zh;
    }

    public void setZh(String zh) {
        this.zh = zh;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "TCountryDic{" +
        ", id=" + id +
        ", en=" + en +
        ", zh=" + zh +
        ", locale=" + locale +
        ", code=" + code +
        "}";
    }

    public CountryDicRes countryDicRes(){
        CountryDicRes res = new CountryDicRes();
        res.setCode(code);
        res.setEn(en);
        res.setZh(zh);
        res.setLocale(locale);
        return res;
    }
}
