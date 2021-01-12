package com.xinlian.common.response;

import com.xinlian.common.enums.CurrencyEnum;
import lombok.Data;

@Data
public class PledgeMiningRes {
    /**
     * 用户uid
     */
    private Long uid;
    /**
     * 账号
     */
    private String userName;
    /**
     * 节点id
     */
    private Integer nodeId;
    /**
     * 节点名称
     */
    private String nodeName;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 真实姓名
     */
    private String authName = "default";
    /**
     * 身份证号
     */
    private String authSn = "default";
    /**
     * 用户头像地址
     */
    private String pic;
    /**
     * 钱包地址
     */
    private String walletAddress;
    /**
     * 货币信息：默认CAT
     */
    private String coin = CurrencyEnum.CAT.getCurrencyCode();
}
