package com.xinlian.admin.biz.service;

import com.xinlian.biz.model.TCurrencyManage;
import com.xinlian.common.response.ResponseResult;

public interface CurrencyManagerService {
    ResponseResult queryAllCurrency();

    ResponseResult udpateCurrencyInfo(TCurrencyManage tCurrencyManage);
}
