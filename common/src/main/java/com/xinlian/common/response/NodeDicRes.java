package com.xinlian.common.response;

import java.util.List;

public class NodeDicRes {
    private Long parentId;
    private String name;
    private Long nodeId;
    private List<NodeDicRes> childes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getNodeId() {
        return nodeId;
    }

    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public List<NodeDicRes> getChildes() {
        return childes;
    }

    public void setChildes(List<NodeDicRes> childes) {
        this.childes = childes;
    }
}
