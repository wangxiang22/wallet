package com.xinlian.admin.server.vo;

import com.baomidou.mybatisplus.annotations.TableField;
import lombok.Data;

@Data
public class AdminOptionsVo {

    /**
     * 配置项
     */
    private String optionName;
    /**
     * 配置的值
     */
    private String optionValue;
    /**
     * 配置项说明
     */
    @TableField("option_tipes")
    private String optionTipes;

    private String belongsSystemCode;
}
