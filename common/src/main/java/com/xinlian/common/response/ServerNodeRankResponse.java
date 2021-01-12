package com.xinlian.common.response;

import lombok.Data;

import java.util.List;

/**
 * com.xinlian.common.response
 *
 * @author by Song
 * @date 2020/2/20 14:53
 */
@Data
public class ServerNodeRankResponse {

    private Long registerNum;
    private String serverNodeName;
    private Long serverNodeId;
    private Long parentId;
    //父级节点集合-字符串
    private String parentIds;
    //该节点汇总子节点后的注册量
    private Long collectionAfterRegisterNum;
    //汇总后-格式化注册量
    private String formatCollectAfterRegisterNum;

    private List<ServerNodeRankResponse> childList;
}
