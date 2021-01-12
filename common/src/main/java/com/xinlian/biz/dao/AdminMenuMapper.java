package com.xinlian.biz.dao;


import com.xinlian.biz.dao.mapper.base.BasicMapper;
import com.xinlian.biz.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface AdminMenuMapper extends BasicMapper<AdminMenuModel,Long> {


    /**
     * 根据角色信息获取对应菜单目录
     * @param roleModel
     * @return
     */
    List<AdminMenuModel> getMenuListByRole(AdminRoleModel roleModel);

    /**
     * 根据角色集合去重获取菜单
     * @param roles
     * @return
     */
    List<AdminMenuModel> getMenuListByRoleModels(List<AdminRoleModel> roles);

    /**
     * 批量加入角色菜单
     * @param refList
     */
    void batchInsertRoleMenu(List<AdminRoleMenuRef> refList);



    /**
     * 根据角色ID集合获取标签列表
     * @param params
     * @return
     */
    List<AdminMenuLabelModel> queryLabelsByRoleId(Map<String, Object> params);

    /**
     * 根据菜单ID获取标签列表
     * @param menuId
     * @return
     */
    List<AdminMenuLabelModel> queryLabelsByMenuId(Long menuId);

    /**
     * 根据角色删除标签权限
     * @param roleId
     */
    void deleteRoleLabelId(Long roleId);


    /**
     * 获取所有菜单集合，roleID 对应的菜单默认选中
     * @param params
     * @return
     */
    List<AdminMenuModel> queryMenusByRoleId(Map<String, Object> params);


    /**
     * 批量加入角色标签
     * @param refList
     */
    void batchInsertRoleLabels(List<AdminRoleLabelRef> refList);

    /**
     * 查询所有label
     * @param list
     * @return
     */
    List<AdminMenuLabelModel> queryAllLabel(List<AdminRoleModel> list);

    /**
     * 新增菜单下标签
     * @param model
     * @return
     */
    Long insertLabel(AdminMenuLabelModel model);

    /**
     * 删除菜单标签
     * @param labelId
     */
    void delLabel(Long labelId);

    /**
     * 修改菜单标签
     * @param model
     */
    void updLabel(AdminMenuLabelModel model);


    List<AdminPermissionModel> queryRoleAndLabels();

    /**
     * 根据当前登录人对应角色列表 - 获取其label集合
     * @param roles
     * @return
     */
    List<AdminPermissionModel> getLabelsByRoleIds(List<AdminRoleModel> roles);
}