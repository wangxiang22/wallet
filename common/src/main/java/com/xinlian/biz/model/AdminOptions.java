package com.xinlian.biz.model;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import lombok.Data;

/**
 * <p>
 * 
 * </p>
 *
 * @author wjf
 * @since 2019-12-26
 */
@TableName("admin_options")
@Data
public class AdminOptions implements Serializable {

	private static final long serialVersionUID = 1L;

	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	 * 配置项
	 */
	@TableField("option_name")
	private String optionName;
	/**
	 * 配置的值
	 */
	@TableField("option_value")
	private String optionValue;
	/**
	 * 配置项说明
	 */
	@TableField("option_tipes")
	private String optionTipes;

	@TableField(exist = false)
	private String belongsSystemCode;

	@TableField("is_show")
	private Integer isShow;

	@Override
	public String toString() {
		return "AdminOptions{" + ", id=" + id + ", optionName=" + optionName + ", optionValue=" + optionValue
				+ ", optionTipes=" + optionTipes + "}";
	}

	public AdminOptions(String belongsSystemCode) {
		super();
		this.belongsSystemCode = belongsSystemCode;
	}

	public AdminOptions() {
		super();
	}

}
