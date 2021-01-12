package com.xinlian.member.biz.service;

import com.baomidou.mybatisplus.service.IService;
import com.xinlian.common.request.AddWithdrawAddressReq;
import com.xinlian.common.request.IdReq;
import com.xinlian.common.request.RechargeCurrencyReq;
import com.xinlian.common.response.CurrencyInfoRes;
import com.xinlian.common.response.PageResult;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.response.WithdrawAddressRes;
import com.xinlian.biz.model.TUserWithdrawAddressRef;

import java.util.List;

/**
 * <p>
 * 客户提币地址表 服务类
 * </p>
 *
 * @author idea
 * @since 2019-12-24
 */
public interface TUserWithdrawAddressRefService extends IService<TUserWithdrawAddressRef> {


    TUserWithdrawAddressRef getByCriteria(TUserWithdrawAddressRef userWithdrawAddressRef);

    /**
     * 添加 提币地址
     * @param req
     * @return
     */
    ResponseResult addAddressRef(AddWithdrawAddressReq req);

    /**
     * 提币地址 列表
     * @param req
     * @return
     */
    PageResult<List<WithdrawAddressRes>> withdrawAddressList(IdReq req);

    /**
     * 删除 提币地址
     * @param req
     * @return
     */
    ResponseResult delWithdrawAddress(IdReq req);

    /**
     * 币种 列表
     * @return
     */
    ResponseResult<List<CurrencyInfoRes>> findCurrencyInfoRes();

    ResponseResult<List<CurrencyInfoRes>> findCurrencyInfoResNew();

    ResponseResult<List<CurrencyInfoRes>> rechargeCurrencyRes(RechargeCurrencyReq req);
}
