package com.xinlian.common.dto;

import com.baomidou.mybatisplus.annotations.TableField;
import lombok.Data;

/**
 * @author lt
 * @date 2020/08/19
 **/
@Data
public class UserUidAuthSnDto {
    /**
     * 用户id
     */
    private Long uid;
    /**
     * 身份证号
     */
    private String authSn;
    /**
     * 手持身份证
     */
    private String authScsfz;
}
