package com.xinlian.biz.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xinlian.biz.model.TUpdateVersion;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author WX
 * @since 2020-04-27
 */
@Component
public interface TUpdateVersionMapper extends BaseMapper<TUpdateVersion> {

	List<TUpdateVersion> queryVersion(Integer type);

	TUpdateVersion queryVersionLimit1(Integer type);

	Integer updateStatus(Integer id);

	List<TUpdateVersion> queryVer();

}
