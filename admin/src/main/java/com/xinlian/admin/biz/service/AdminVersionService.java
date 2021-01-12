package com.xinlian.admin.biz.service;

import com.xinlian.biz.model.AdminUpdateVersionInfo;
import com.xinlian.biz.model.TUpdateVersion;
import com.xinlian.common.request.PageReq;
import com.xinlian.common.request.VersionDataReq;
import com.xinlian.common.response.PageResult;
import com.xinlian.common.response.ResponseResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AdminVersionService{

    PageResult<List<TUpdateVersion>> queryVersion(PageReq pageReq);

    PageResult<List<AdminUpdateVersionInfo>> queryVersionData(VersionDataReq versionDataReq);

    ResponseResult addVersion(TUpdateVersion tUpdateVersion);

    ResponseResult updateVersion(TUpdateVersion tUpdateVersion);

    ResponseResult queryVersionTime(VersionDataReq versionDataReq);

    ResponseResult queryNewVersion(VersionDataReq versionReq);

    ResponseResult deleteVersion(VersionDataReq versionReq);

}
