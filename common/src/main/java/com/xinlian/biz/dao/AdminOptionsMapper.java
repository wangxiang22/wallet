package com.xinlian.biz.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xinlian.biz.model.AdminOptions;

@Component
public interface AdminOptionsMapper extends BaseMapper<AdminOptions> {

	String getAdminOptionValueByKey(@Param(value = "optionName") String optionName);

	List<AdminOptions> queryByBelongsSystemCode(AdminOptions adminOptions);

	AdminOptions queryByBelongsSystemCodeLimit1(AdminOptions adminOptions);

	Long batchUpdateModel(List<AdminOptions> list);
}
