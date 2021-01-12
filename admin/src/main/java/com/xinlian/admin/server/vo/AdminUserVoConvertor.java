package com.xinlian.admin.server.vo;

import com.xinlian.biz.model.AdminUser;
import com.xinlian.common.Base.BaseVoConvertor;
import com.xinlian.common.enums.AdminUserStatusEnum;
import com.xinlian.common.utils.DateFormatUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

/**
 * com.xinlian.admin.server.vo
 *
 * @author by Song
 * @date 2020/2/23 18:37
 */
@Slf4j
public class AdminUserVoConvertor extends BaseVoConvertor<AdminUserVo, AdminUser> {

    @Override
    public AdminUserVo convert(AdminUser bo) throws Exception {
        AdminUserVo vo = new AdminUserVo();
        try {
            BeanUtils.copyProperties(vo,bo);
            vo.setStatusName(AdminUserStatusEnum.getEnumDesc(bo.getStatus()));
            vo.setCreateTime(DateFormatUtil.formatTillSecond(bo.getCreateTime()));
            vo.setUpdateTime(DateFormatUtil.formatTillSecond(bo.getUpdateTime()));
        }catch (Exception e){
            log.error("adminUserVo转换异常,{}",e.toString(),e);
        }
        return vo;
    }
}
