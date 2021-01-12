package com.xinlian.biz.dao;

import com.xinlian.biz.model.CurrencyStateBalance;
import com.xinlian.biz.model.TCurrencyManage;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xinlian.biz.model.UserCurrencyStateReq;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 币种管理表 Mapper 接口
 * </p>
 *
 * @since 2019-12-23
 */
@Component
public interface TCurrencyManageMapper extends BaseMapper<TCurrencyManage> {


    TCurrencyManage getCurrencyManageByCurrencyAddress(String currencyAddress);

    TCurrencyManage getCurrencyManageByCurrencyId(Long currencyId);

    CurrencyStateBalance queryBalance(UserCurrencyStateReq userCurrencyStateReq);
}
