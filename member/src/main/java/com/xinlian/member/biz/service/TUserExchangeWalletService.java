package com.xinlian.member.biz.service;

import com.baomidou.mybatisplus.service.IService;
import com.xinlian.common.request.DelBindReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.biz.model.TUserExchangeWallet;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wjf
 * @since 2019-12-28
 */
public interface TUserExchangeWalletService extends IService<TUserExchangeWallet> {

    ResponseResult queryBindState(Long uid);

    ResponseResult BindExchange(TUserExchangeWallet tUserExchangeWallet);

    ResponseResult delBindReq(DelBindReq delBindReq);
}
