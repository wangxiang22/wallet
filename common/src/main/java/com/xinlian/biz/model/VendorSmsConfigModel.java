package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 短信厂商配置表
 * </p>
 *
 * @author Song
 * @since 2020-07-08
 */
@Data
@Accessors(chain = true)
public class VendorSmsConfigModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 配置项
     */
    @TableField("option_name")
    private String optionName;
    /**
     * 配置值
     */
    @TableField("option_value")
    private String optionValue;
    /**
     * 配置项说明
     */
    @TableField("option_tipes")
    private String optionTipes;
    /**
     * 所属短信厂商
     */
    @TableField("belongs_system_code")
    private String belongsSystemCode;
    /**
     * 1可用，2不可用
     */
    @TableField("is_show")
    private Integer isShow;
    /**
     * 自动切换排序
     */
    @TableField("sort")
    private Integer sort;


}
