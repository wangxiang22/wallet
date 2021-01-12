package com.xinlian.biz.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xinlian.biz.model.AdminBillClassify;
import com.xinlian.common.request.BillClassifyShowHideReq;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 账单分类表 Mapper 接口
 * </p>
 *
 * @author lt
 * @since 2020-07-30
 */
@Component
public interface AdminBillClassifyMapper extends BaseMapper<AdminBillClassify> {
    /**
     * 修改单个账单分类是否展示
     * @param req 展示属性
     * @return 修改成功数
     */
    int updateShowHide(BillClassifyShowHideReq req);
}
