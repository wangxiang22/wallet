package com.xinlian.biz.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xinlian.biz.model.AdminRoleInterface;
import com.xinlian.biz.model.AdminRoleModel;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 角色与接口关联 Mapper 接口
 * </p>
 *
 * @author 无名氏
 * @since 2020-08-22
 */
@Component
public interface AdminRoleInterfaceMapper extends BaseMapper<AdminRoleInterface> {

    List<String> getInterfaceReqUrlByRoleIds(List<AdminRoleModel> list);

}
