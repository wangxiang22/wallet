package com.xinlian.biz.utils;

import com.xinlian.biz.dao.TServerNodeMapper;
import com.xinlian.biz.model.TServerNode;
import com.xinlian.common.exception.ReqParamException;
import com.xinlian.common.redis.RedisKeys;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class NodeVoyageUtil {
	@Autowired
	private TServerNodeMapper serverNodeMapper;
	@Autowired
	private CommonRedisClient commonRedisClient;

	/**
	 * 节点是否属于大航海节点
	 * 
	 * @param nodeId 需要判断的节点id
	 * @return 属于：true，不属于：false
	 */
	public boolean belongVoyageNode(@NonNull Long nodeId) {
		try {
			List<Long> nodeChildIds = commonRedisClient.get(RedisKeys.NODE_VOYAGE);
			if (null == nodeChildIds || nodeChildIds.size() == 0) {
				// 获取大航海节点及其下面的所有节点id
				TServerNode tServerNode = serverNodeMapper.selectById(118L);// TODO 写死ID，有点诡异

				if (null == tServerNode || StringUtils.isBlank(tServerNode.getChildIds())) {
					return false;
				}
				Long[] nodeIds = (Long[]) ConvertUtils.convert(tServerNode.getChildIds().split(","), Long[].class);
				nodeChildIds = Arrays.stream(nodeIds).collect(Collectors.toList());
				if (nodeChildIds.size() == 0) {
					return false;
				}
				commonRedisClient.set(RedisKeys.NODE_VOYAGE, nodeChildIds);
			}
			// 判断参数节点是否属于大航海节点
			return nodeChildIds.contains(nodeId);
		} catch (Exception e) {
			log.error("获取节点信息失败：{}", e.toString(), e);
			throw new ReqParamException();
		}
	}

	public Long getSeaPatrolNodeId(@NonNull Long nodeId) {
		if(belongVoyageNode(nodeId)){
			return 118L;
		}else {
			return 0L;
		}
	}
}
