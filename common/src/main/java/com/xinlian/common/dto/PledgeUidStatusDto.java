package com.xinlian.common.dto;

import lombok.Data;

@Data
public class PledgeUidStatusDto {
    /**
     * 用户uid
     */
    private Long uid;
    /**
     * 请求算力地球接口推送状态 - 1：推送成功，2：推送失败，3：待再次推送（此数值手动修改）
     */
    private Integer status;
}
