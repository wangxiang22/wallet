package com.xinlian.member.biz.service;

import com.baomidou.mybatisplus.service.IService;
import com.xinlian.biz.model.TUserInfo;

/**
 * com.xinlian.member.biz.service
 *
 * @date 2020/2/13 10:52
 */
public interface UserInfoService extends IService<TUserInfo> {

    TUserInfo getOneModel(TUserInfo userInfo);
}
