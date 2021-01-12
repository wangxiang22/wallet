package com.xinlian.common.request;

import com.baomidou.mybatisplus.annotations.TableField;
import com.xinlian.common.result.BizException;
import lombok.Data;

/**
 * @author lt
 * @date 2020/09/01
 **/
@Data
public class UpdateNewOrderReq implements ICheckParam {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 收货地址
     */
    private String address;
    /**
     * 收件人手机号
     */
    private String phone;
    /**
     * 收件人
     */
    private String userName;
    /**
     * 链区
     */
    private String chainName;


    @Override
    public void checkParam() {
        if (id == null) {
            throw new BizException("id不能为空!");
        }
    }
}
