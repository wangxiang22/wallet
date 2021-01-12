package com.xinlian.biz.dao;

import com.xinlian.biz.model.AdminUpdateVersionInfo;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author WX
 * @since 2020-04-29
 */
@Service
public interface AdminUpdateVersionInfoMapper extends BaseMapper<AdminUpdateVersionInfo> {
    Integer updateDownload(AdminUpdateVersionInfo adminUpdateVersionInfo);
}
