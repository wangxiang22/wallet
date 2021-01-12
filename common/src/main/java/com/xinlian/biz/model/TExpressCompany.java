package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName(value = "t_express_company")
public class TExpressCompany implements Serializable {

    private static final long serialVersionUID = 2689595907491994615L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 快递厂商
     */
    @TableField(value = "com")
    private String com;

    /**
     * 代码
     */
    @TableField(value = "number")
    private String number;

    /**
     * 官网
     */
    @TableField(value = "site")
    private String site;

    /**
     * 图片
     */
    @TableField(value = "src")
    private String src;

    /**
     * 缩写
     */
    @TableField(value = "letter")
    private String letter;

    /**
     * 电话
     */
    @TableField(value = "tel")
    private String tel;

    /**
     * 快递编码
     */
    @TableField(value = "comid")
    private String comid;

    /**
     * 状态 0显示 1隐藏
     */
    @TableField(value = "status")
    private Integer status;

}
