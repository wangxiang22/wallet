package com.xinlian.common.response;

import lombok.Data;

@Data
public class ChainOwnerNumberRes {
    private Long nodeId;//节点id
    private String nodeName;//节点名称
    private Long nodeChainOwnerNum;//节点链权人数量
}
