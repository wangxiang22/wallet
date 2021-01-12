package com.xinlian.common.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServerNodeReq {
    private Integer node_type;//需要返回的节点信息筛选 - 0：返回所有节点信息，1：返回除新大陆及其子节点以外的节点信息，2：只返回新大陆及其子节点的信息
    private Long parentId;//节点上级id
    private Long id;//节点id
}
