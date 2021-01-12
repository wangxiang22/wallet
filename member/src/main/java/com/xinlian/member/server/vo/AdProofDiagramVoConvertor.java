package com.xinlian.member.server.vo;

import com.xinlian.biz.model.AdProofDiagramModel;
import com.xinlian.common.Base.BaseVoConvertor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

/**
 * @author Song
 * @date 2020-05-18 11:04
 * @description 人机广告验证VoConvertor
 */
@Slf4j
public class AdProofDiagramVoConvertor extends BaseVoConvertor<AdProofDiagramVo, AdProofDiagramModel> {


    @Override
    public AdProofDiagramVo convert(AdProofDiagramModel bo) throws Exception {
        AdProofDiagramVo vo = new AdProofDiagramVo();
        try{
            BeanUtils.copyProperties(vo,bo);
        }catch (Exception e){
            log.error("人机广告验证vo转换异常:{}", e.toString(), e);
        }
        return vo;
    }
}
