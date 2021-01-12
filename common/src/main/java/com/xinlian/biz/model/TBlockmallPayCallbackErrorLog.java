package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_blockmall_pay_callback_error_log")
public class TBlockmallPayCallbackErrorLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("repeate_num")
    private Integer repeateNum;

    @TableField("callback_time")
    private Date callbackTime;

    @TableField("callback")
    private String callback;

    @TableField("order_no")
    private String orderNo;

    @TableField("data")
    private String data;

    @TableField("sign")
    private String sign;

    @TableField("err_msg")
    private String errMsg;


}
