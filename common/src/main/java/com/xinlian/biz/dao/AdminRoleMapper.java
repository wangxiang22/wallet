package com.xinlian.biz.dao;

import com.xinlian.biz.dao.mapper.base.BasicMapper;
import com.xinlian.biz.model.AdminRoleModel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public interface AdminRoleMapper extends BasicMapper<AdminRoleModel,Long> {

    /**
     * 根据用户id获取对应角色集合
     * @param userId 用户id
     * @return
     * @throws Exception
     */
    List<AdminRoleModel> getRoleByUserId(Long userId);

    /**
     * 删除角色对应menuid
     * @param adminRoleId
     */
    void deleteRoleMenuId(@Param(value = "adminRoleId")Long adminRoleId);



}
