package com.xinlian.biz.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xinlian.biz.model.VendorSmsConfigModel;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 短信厂商配置表 Mapper 接口
 * </p>
 *
 * @author Song
 * @since 2020-07-08
 */
@Component
public interface VendorSmsConfigMapper extends BaseMapper<VendorSmsConfigModel> {

    /**
     * 根据所属短信厂商来获取对应信息
     * @param vendorSmsConfigModel
     * @return
     */
    List<VendorSmsConfigModel> querySmsConfigSystemCode(VendorSmsConfigModel vendorSmsConfigModel);

}
