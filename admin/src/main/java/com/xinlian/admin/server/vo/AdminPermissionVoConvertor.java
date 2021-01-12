package com.xinlian.admin.server.vo;

import com.xinlian.biz.model.AdminPermissionModel;
import com.xinlian.common.Base.BaseVoConvertor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

/**
 * com.xinlian.admin.server.vo
 *
 * @author by Song
 * @date 2020/2/25 22:14
 */
@Slf4j
public class AdminPermissionVoConvertor extends BaseVoConvertor<AdminPermissionVo, AdminPermissionModel> {

    @Override
    public AdminPermissionVo convert(AdminPermissionModel bo) throws Exception {
        AdminPermissionVo vo = new AdminPermissionVo();
        try{
            BeanUtils.copyProperties(vo,bo);
        }catch (Exception e){
            e.printStackTrace();
        }
        return vo;
    }
}
