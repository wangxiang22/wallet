package com.xinlian.member.biz.service;

import com.xinlian.biz.model.TMiningApply;
import com.baomidou.mybatisplus.service.IService;
import com.xinlian.common.request.FindAllUserReq;

/**
 * <p>
 * 挖矿申请表 服务类
 * </p>
 *
 * @author 无名氏
 * @since 2020-04-25
 */
public interface TMiningApplyService extends IService<TMiningApply> {

    void mingingApply(TMiningApply tMiningApply);

    TMiningApply findUserApplyState();
}
