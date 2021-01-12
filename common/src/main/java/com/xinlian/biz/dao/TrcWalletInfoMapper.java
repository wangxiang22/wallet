package com.xinlian.biz.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xinlian.biz.model.TWalletInfo;
import com.xinlian.biz.model.TrcWalletInfoModel;
import org.springframework.stereotype.Repository;


@Repository
public interface TrcWalletInfoMapper extends BaseMapper<TWalletInfo> {

    int allocationCurrencyAddress(TrcWalletInfoModel trcWalletInfoModel);

    TrcWalletInfoModel getTrcWalletInfo(TrcWalletInfoModel whereModel);

}
