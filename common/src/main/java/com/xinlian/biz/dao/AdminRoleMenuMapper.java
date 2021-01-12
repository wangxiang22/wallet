package com.xinlian.biz.dao;


import com.xinlian.biz.dao.mapper.base.BasicMapper;
import com.xinlian.biz.model.AdminRoleMenuRef;

public interface AdminRoleMenuMapper extends BasicMapper<AdminRoleMenuRef,Long> {

    /**
     * 根据角色id删除 角色对应菜单关系
     * @param roleId
     */
    void deleteByRoleId(Long roleId);
}