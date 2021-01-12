package com.xinlian.member.server.vo;

import com.xinlian.biz.model.TServerNode;
import com.xinlian.common.Base.BaseVoConvertor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

@Data
@Slf4j
public class ServerNodeVoConvertor extends BaseVoConvertor<ServerNodeVo, TServerNode> {


    @Override
    public ServerNodeVo convert(TServerNode bo)  {
        ServerNodeVo vo = new ServerNodeVo();
        try{
            BeanUtils.copyProperties(vo,bo);
        }catch (Exception e){
            log.error("--:{}", e.toString(), e);
        }
        return vo;
    }
}
