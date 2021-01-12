package com.xinlian.member.biz.service;

import com.baomidou.mybatisplus.service.IService;
import com.xinlian.biz.model.TOrderInfo;
import com.xinlian.common.request.QuerySpotInfoReq;
import com.xinlian.common.response.ResponseResult;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lx
 * @since 2020-06-19
 */
public interface TOrderInfoService extends IService<TOrderInfo> {

    ResponseResult querySpotInfo(QuerySpotInfoReq querySpotInfo);

    ResponseResult queryEveryOrderList(QuerySpotInfoReq querySpotInfo);


    ResponseResult queryBuyerInfoByAddr(String addr);
}
