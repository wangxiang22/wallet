package com.xinlian.biz.dao;


import com.xinlian.biz.model.AdProofDiagramModel;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 广告人机校验表 Mapper 接口
 * </p>
 *
 * @author WX
 * @since 2020-05-16
 */
@Component
public interface AdProofDiagramMapper {

    /**
     * 随机获取某个实体bean
     * @return
     */
    AdProofDiagramModel getRandomOneAd();
}
