package com.xinlian.member.biz.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.xinlian.biz.dao.TUserInfoMapper;
import com.xinlian.biz.model.TUserInfo;
import com.xinlian.member.biz.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * com.xinlian.member.biz.service.impl
 *
 * @author by Song
 * @date 2020/2/13 10:54
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<TUserInfoMapper, TUserInfo> implements UserInfoService {

    @Autowired
    private TUserInfoMapper userInfoMapper;

    @Override
    public TUserInfo getOneModel(TUserInfo userInfo) {
        return userInfoMapper.getOneModel(userInfo);
    }
}
