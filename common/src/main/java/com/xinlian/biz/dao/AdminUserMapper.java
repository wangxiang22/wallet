package com.xinlian.biz.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xinlian.biz.model.AdminUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AdminUserMapper extends BaseMapper<AdminUser> {

    List<AdminUser> query(AdminUser adminUser);

    String getEmailByUserName(@Param(value = "userName")String userName);
}
