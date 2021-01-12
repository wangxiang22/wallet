package com.xinlian.member.biz.service;

import com.xinlian.biz.model.TCurrencyManage;

/**
 * <p>
 * 币种管理表 服务类
 * </p>
 *
 * @since 2019-12-23
 */
public interface TCurrencyManageService {

    TCurrencyManage getCurrencyManageByCurrencyId(Long currencyId);


}
