package com.xinlian.biz.model.gson;

import lombok.Data;

import java.util.List;

/**
 * @author lt
 * @date 2020/09/24
 **/
@Data
public class Root {

    private String name;

    private List<ChildrenList> children;
}
