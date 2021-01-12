package com.xinlian.admin.biz.service;

import com.xinlian.biz.dao.AdminMenuMapper;
import com.xinlian.biz.model.AdminMenuModel;
import com.xinlian.biz.model.AdminPermissionModel;
import com.xinlian.biz.model.AdminRoleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * com.xinlian.admin.biz.service
 *
 * @author by Song
 * @date 2020/2/21 21:54
 */
@Service
public class AdminMenuService {

    @Autowired
    private AdminMenuMapper adminMenuMapper;

    /**
     * 获取左侧菜单目录
     * @param roles
     * @return
     */
    public List<AdminMenuModel> getMenuListByRoleModels(List<AdminRoleModel> roles){
        List<AdminMenuModel> menuModelList = new ArrayList<AdminMenuModel>();
        List<AdminMenuModel> rootMenu = adminMenuMapper.getMenuListByRoleModels(roles);
        //先拿到第一级菜单
        for (int i=0;i<rootMenu.size();i++){
            //一级菜单没有parentId
            if(StringUtils.isEmpty(rootMenu.get(i).getParentMenuId())){
                menuModelList.add(rootMenu.get(i));
            }
        }
        // 为一级菜单设置子菜单，getChild是递归调用的
        for (AdminMenuModel menu : menuModelList) {
            menu.setChildMenus(getChild(menu.getId(), rootMenu));
        }
        return menuModelList;
    }

    /**
     * 递归查找子菜单
     * @param id 当前菜单id
     * @param rootMenu 要查找的列表
     * @return
     */
    private List<AdminMenuModel> getChild(Long id, List<AdminMenuModel> rootMenu) {
        // 子菜单
        List<AdminMenuModel> childList = new ArrayList<>();
        for (AdminMenuModel menu : rootMenu) {
            // 遍历所有节点，将父菜单id与传过来的id比较
            if (!StringUtils.isEmpty(menu.getParentMenuId())) {
                if (menu.getParentMenuId().equals(id)) {
                    childList.add(menu);
                }
            }
        }
        // 把子菜单的子菜单再循环一遍
        for (AdminMenuModel menu : childList) {// 没有url子菜单还有子菜单
            if (!StringUtils.isEmpty(menu.getMenuUrl())) {
                // 递归
                menu.setChildMenus(getChild(menu.getId(), rootMenu));
            }
        } // 递归退出条件
        if (childList.size() == 0) {
            return null;
        }
        return childList;
    }

    /**
     * 根据当前登录人对应角色列表 - 获取其label集合
     * @param roles
     * @return
     */
    public List<AdminPermissionModel> getLabelsByRoleIds(List<AdminRoleModel> roles) {
        return adminMenuMapper.getLabelsByRoleIds(roles);
    }

    public List<AdminMenuModel> queryMenusByRoleId(Map<String,Object> params) {
        return adminMenuMapper.queryMenusByRoleId(params);
    }
}
