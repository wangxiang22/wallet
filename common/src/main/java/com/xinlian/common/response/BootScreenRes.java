package com.xinlian.common.response;

import lombok.Data;

@Data
public class BootScreenRes {
    private String bootScreenUrl;//钱包app启动界面图片
    private String bootScreenStatus;//钱包app启动界面图片是否启用 - 0：关闭，1：开启
}
