package com.xinlian.member.biz.service;

import com.baomidou.mybatisplus.service.IService;
import com.xinlian.biz.model.TUpdateVersion;
import com.xinlian.common.request.VersionDataReq;
import com.xinlian.common.response.ResponseResult;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author WX
 * @since 2020-04-27
 */
public interface TUpdateVersionService extends IService<TUpdateVersion> {

	ResponseResult queryVersion(VersionDataReq versionReq);

	ResponseResult updateIssuedToSql();

	/* ResponseResult updateInstallToSql(); */

	TUpdateVersion queryVersion(Integer type);

}
