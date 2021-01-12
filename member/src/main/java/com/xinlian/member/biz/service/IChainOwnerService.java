package com.xinlian.member.biz.service;

import com.xinlian.common.request.ChainOwnerReq;
import com.xinlian.common.response.ResponseResult;

public interface IChainOwnerService {

    /**
     * 获取链权人相关实名信息
     * @param uid 用户id
     * @return
     */
    ResponseResult findChainOwnerUser(Long uid);

    /**
     * 上传链权人填写的相关信息||保存链权人证书url地址
     * @param chainOwnerReq
     * @return
     */
    ResponseResult updateChainOwnerUser(ChainOwnerReq chainOwnerReq);

    /**
     * 查询链权人资产
     * @param uid
     * @return
     */
    ResponseResult findChainOwnerAsset(Long uid);
}
