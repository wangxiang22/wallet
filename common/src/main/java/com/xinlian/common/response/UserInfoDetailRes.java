package com.xinlian.common.response;

import lombok.Data;

import java.util.List;

@Data
public class UserInfoDetailRes extends UserInfoRes{
    private List<CurrencyInfoRes> walletInfos;
}
