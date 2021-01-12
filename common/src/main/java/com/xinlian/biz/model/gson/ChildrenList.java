package com.xinlian.biz.model.gson;

import lombok.Data;


import java.util.List;

/**
 * @author lt
 * @date 2020/09/24
 **/
@Data
public class ChildrenList<T> {

    private String name;

    private List<T> children;

}
