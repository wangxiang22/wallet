package com.xinlian.common.Base;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public abstract class BaseVoConvertor<V,B> {


    public abstract V convert(B bo) throws Exception;

    public List<V> convertList(List<B> boList)  throws Exception {
        List<V> voList = new ArrayList<V>();
        for(B bo : boList) {
            V vo = this.convert(bo);
            voList.add(vo);
        }
        return voList;
    }

}
