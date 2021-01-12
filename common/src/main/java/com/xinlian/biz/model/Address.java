package com.xinlian.biz.model;

import com.alibaba.fastjson.JSON;
import lombok.Data;

@Data
public class Address {
    private String address;
    private int coinType;

    public static com.spark.bipay.entity.Address parse(String json){
        return JSON.parseObject(json, com.spark.bipay.entity.Address.class);
    }
}
