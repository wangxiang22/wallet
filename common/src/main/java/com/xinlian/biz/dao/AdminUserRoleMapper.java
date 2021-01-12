package com.xinlian.biz.dao;


import com.xinlian.biz.dao.mapper.base.BasicMapper;
import com.xinlian.biz.model.AdminUserRoleRef;
import org.springframework.stereotype.Component;

@Component
public interface AdminUserRoleMapper extends BasicMapper<AdminUserRoleRef,Long> {


    /**
     * 根据角色id删除 角色对应用户关系
     * @param roleId
     */
    void deleteByRoleId(Long roleId);

    /**
     * 根据用户ID修改对应角色ID
     */
    void updateRoleByUserId(AdminUserRoleRef userRoleRef);
}