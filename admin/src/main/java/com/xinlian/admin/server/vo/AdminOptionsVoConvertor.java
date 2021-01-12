package com.xinlian.admin.server.vo;

import com.xinlian.biz.model.AdminOptions;
import com.xinlian.common.Base.BaseVoConvertor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

@Slf4j
public class AdminOptionsVoConvertor extends BaseVoConvertor<AdminOptionsVo, AdminOptions> {


    @Override
    public AdminOptionsVo convert(AdminOptions bo) throws Exception {
        AdminOptionsVo vo = new AdminOptionsVo();
        try{
            BeanUtils.copyProperties(vo,bo);
        }catch (Exception e){
            log.error("配置项转换出现异常");
        }
        return vo;
    }
}
