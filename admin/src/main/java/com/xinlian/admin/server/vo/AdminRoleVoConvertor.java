package com.xinlian.admin.server.vo;

import com.xinlian.biz.model.AdminRoleModel;
import com.xinlian.common.Base.BaseVoConvertor;
import lombok.extern.slf4j.Slf4j;

/**
 * com.xinlian.admin.server.vo
 *
 * @author by Song
 * @date 2020/2/23 16:00
 */
@Slf4j
public class AdminRoleVoConvertor extends BaseVoConvertor<AdminRoleVo, AdminRoleModel> {

    @Override
    public AdminRoleVo convert(AdminRoleModel bo) throws Exception {
        AdminRoleVo adminRoleVo = new AdminRoleVo();
        try {
            adminRoleVo.setId(bo.getId());
            adminRoleVo.setRoleCode(bo.getRoleCode());
            adminRoleVo.setRoleName(bo.getRoleName());
        }catch (Exception e){
            log.error("角色转换出现异常");
        }
        return adminRoleVo;
    }
}
