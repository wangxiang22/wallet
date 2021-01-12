package com.xinlian.admin.biz.service;

import com.xinlian.biz.model.AdminOptions;
import com.xinlian.common.request.AdminOptionsReq;
import com.xinlian.common.request.PageReq;
import com.xinlian.common.response.PageResult;
import com.xinlian.common.response.ResponseResult;

import java.util.List;

public interface AdminOptionsService {

    /**
     * 查询全部配置项信息（分页）
     * @return
     */
    PageResult<List<AdminOptions>> findOptionsListPage(PageReq pageReq);

    /**
     * 根据配置项id查询
     * @param adminOptionsReq
     * @return
     */
    ResponseResult findOptionById(AdminOptionsReq adminOptionsReq);

    /**
     * 新增配置项
     * @param adminOptions
     * @return
     */
    ResponseResult createOption(AdminOptions adminOptions);

    /**
     * 修改配置项
     * @param adminOptions
     * @return
     */
    ResponseResult updateOption(AdminOptions adminOptions);

    /**
     * 根据配置项id删除
     * @param adminOptionsReq
     * @return
     */
    ResponseResult deleteOption(AdminOptionsReq adminOptionsReq);

    /**
     * 查询BelongsSystemCode相关的
     * @param adminOptions
     * @return
     */
    List<AdminOptions> queryByBelongsSystemCode(AdminOptions adminOptions);

    /**
     * 批量更新配置项信息
     * @param adminOptionsList
     */
    void batchUpdateModel(List<AdminOptions> adminOptionsList);

    /**
     * 移除配置项redis缓存
     * @param belongsSystemCodeValue
     * @param adminOptionsList
     */
    void removeRedisCache(String belongsSystemCodeValue, List<AdminOptions> adminOptionsList);

}