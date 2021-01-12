package com.xinlian.admin.biz.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.xinlian.admin.biz.service.PushNoticeManagerService;
import com.xinlian.biz.dao.TPushNoticeMapper;
import com.xinlian.biz.dao.TServerNodeMapper;
import com.xinlian.biz.dao.TUserInfoMapper;
import com.xinlian.biz.model.TPushNotice;
import com.xinlian.biz.model.TServerNode;
import com.xinlian.biz.model.TUserInfo;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.request.PageReq;
import com.xinlian.common.response.PageResult;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.utils.JPushUtil;
import com.xinlian.common.utils.ListUtil;
import com.xinlian.common.utils.UniqueNoUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lt
 */
@Slf4j
@Service
public class PushNoticeManagerServiceImpl implements PushNoticeManagerService {
    @Autowired
    private TUserInfoMapper userInfoMapper;
    @Autowired
    private TServerNodeMapper serverNodeMapper;
    @Autowired
    private TPushNoticeMapper pushNoticeMapper;

    @Override
    public ResponseResult createPushNotice(TPushNotice tPushNotice) {
        //先判断是否有正在上线的推送
        Integer count = pushNoticeMapper.selectCount(new EntityWrapper<TPushNotice>().eq("online_status", 1));
        if (count > 0) {
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).msg("列表中已有正在上线的推送，请下线后再添加新推送").build();
        }
        //线上状态在添加时默认为上线
        tPushNotice.setOnlineStatus(1);
        int i = tPushNotice.getStartTime().compareTo(new Date());
        //新增推送只会有两个推送状态：1：未开始，2：生效中
        if (i > 0) {
            tPushNotice.setStatus(1);
        } else {
            tPushNotice.setStatus(2);
        }
        //该字段在极光推送中必填，移动端用不到这个字段，写死
        String msgContent = "msgContent";
        Map<String, String> map = createPushMap(tPushNotice);
        //极光定时推送id
        StringBuffer scheduleIds = new StringBuffer();
        if (null != tPushNotice.getUids() && !"".equals(tPushNotice.getUids())) {
            //根据uids获取jid列表，发送推送
            List<String> jidList = getJidListWithUid(tPushNotice.getUids());
            int num = jidList.size() % 950 == 0 ? jidList.size() / 950 : jidList.size() / 950 + 1;
            //极光id列表一次不能超过1000个，API调用频率不能超过100次/分钟
            List<List<String>> subLists = ListUtil.averageAssign(jidList, num);
            for (int j = 0; j < subLists.size(); j++) {
                ResponseResult responseResult = JPushUtil.sendScheduleToRegistrationListMessage(subLists.get(j), msgContent, map, tPushNotice.getTitle(), tPushNotice.getStartTime(), tPushNotice.getEndTime(), tPushNotice.getTitle());
                if (200 != responseResult.getCode()) {
                    return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).msg("极光推送发送失败").build();
                }
                scheduleIds.append(j == 0? (String) responseResult.getResult() : "," + responseResult.getResult());
            }
        }else if (null != tPushNotice.getNodeIds() && !"".equals(tPushNotice.getNodeIds())) {
            //根据nodeIds获取jid列表，发送推送（传过来一级节点id，需要处理下面的子节点id信息）
            List<String> jidList = getJidListWithNodeId(tPushNotice.getNodeIds());
            int num = jidList.size() % 950 == 0 ? jidList.size() / 950 : jidList.size() / 950 + 1;
            //极光id列表一次不能超过1000个，API调用频率不能超过100次/分钟
            List<List<String>> subLists = ListUtil.averageAssign(jidList, num);
            for (int j = 0; j < subLists.size(); j++) {
                if(j != 0 && j % 100 == 0) {
                    //线程睡一分钟
                    Thread thread = new MySleepThread();
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        log.info("线程阻塞出错：{}",e.toString());
                    }
                }
                ResponseResult responseResult = JPushUtil.sendScheduleToRegistrationListMessage(subLists.get(j), msgContent, map, tPushNotice.getTitle(), tPushNotice.getStartTime(), tPushNotice.getEndTime(), tPushNotice.getTitle());
                if (200 != responseResult.getCode()) {
                    return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).msg("极光推送发送失败").build();
                }
                scheduleIds.append(j == 0? (String) responseResult.getResult() : "," + responseResult.getResult());
            }
        }else {
            //推送给所有用户
            ResponseResult responseResult = JPushUtil.sendScheduleToAllMessage(msgContent, map, tPushNotice.getTitle(), tPushNotice.getStartTime(), tPushNotice.getEndTime(), tPushNotice.getTitle());
            if (200 != responseResult.getCode()) {
                return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).msg("极光推送发送失败").build();
            }
            scheduleIds.append((String) responseResult.getResult());
        }
        tPushNotice.setScheduleId(scheduleIds.toString());
        Integer insertResult = pushNoticeMapper.insert(tPushNotice);
        if (0 == insertResult) {
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).msg("新增推送消息失败").build();
        }
        return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).build();
    }

    /**
     * 线程睡一分钟
     */
    class MySleepThread extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(1000*60L);
            }catch (Exception e) {
                log.info("线程sleep出错：{}",e.toString());
            }

        }
    }

    /**
     * 创建极光自定义推送扩展字段
     * @param tPushNotice 推送请求参数实体
     * @return 自定义推送扩展字段map
     */
    private Map<String,String> createPushMap(TPushNotice tPushNotice) {
        String uuid = UniqueNoUtil.uuid();
        //设置唯一标识码
        tPushNotice.setUniqueCode(uuid);
        Map<String,String> map = new HashMap<>(6);
        map.put("title",tPushNotice.getTitle());
        map.put("type",String.valueOf(tPushNotice.getType()));
        map.put("closeStatus",String.valueOf(tPushNotice.getCloseStatus()));
        map.put("uuid",uuid);
        if (1 == tPushNotice.getType()) {
            //图片推送
            map.put("image",tPushNotice.getPushImage());
            map.put("url",tPushNotice.getPushUrl());
        }
        if (2 == tPushNotice.getType()) {
            //文字推送
            map.put("text",tPushNotice.getPushText());
        }
        if (3 == tPushNotice.getType()) {
            //全屏推送
            map.put("fullScreen",tPushNotice.getPushUrl());
        }
        return map;
    }

    @Override
    public PageResult<List<TPushNotice>> findPushNoticeListPage(PageReq pageReq) {
        PageResult<List<TPushNotice>> result = new PageResult<>();
        //判断线上状态：如果是下线则不需要修改，如果是上线，对比推送状态
        //查看时推送状态只会有两种 - 2：生效中，3：已失效（在开始和结束时间之间则为生效中；在结束时间之后则为已失效，同时改为下线状态）
        List<TPushNotice> pushNoticeList = pushNoticeMapper.selectList(new EntityWrapper<TPushNotice>().eq("online_status", 1));
        for (TPushNotice pushNotice : pushNoticeList) {
            //推送状态未开始，如果当前时间大于开始时间小于结束时间，修改为生效中；如果大于结束时间，修改为已失效，并将上线改为下线
            if (1 == pushNotice.getStatus()) {
                int i = pushNotice.getStartTime().compareTo(new Date());
                int j = pushNotice.getEndTime().compareTo(new Date());
                if (i < 0 && j > 0) {
                    pushNotice.setStatus(2);
                }
                if (j < 0) {
                    pushNotice.setStatus(3);
                    pushNotice.setOnlineStatus(0);
                }
            }
            //推送状态生效中，如果当前时间大于结束时间，修改为已失效，并将上线改为下线
            if (2 == pushNotice.getStatus()) {
                int i = pushNotice.getEndTime().compareTo(new Date());
                if (i < 0) {
                    pushNotice.setStatus(3);
                    pushNotice.setOnlineStatus(0);
                }
            }
            Integer updateResult = pushNoticeMapper.updateById(pushNotice);
            if (0 == updateResult) {
                result.setCode(GlobalConstant.ResponseCode.FAIL);
                result.setMsg("实时更新推送通知状态失败");
                return result;
            }
        }
        result.setTotal(pushNoticeMapper.selectCount(null));
        result.setCurPage(pageReq.pickUpCurPage());
        result.setPageSize(pageReq.pickUpPageSize());
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        result.setResult(pushNoticeMapper.selectPage(new Page<TPushNotice>((int) pageReq.pickUpCurPage(),(int) pageReq.pickUpPageSize()),
                new EntityWrapper<TPushNotice>().orderBy("id",false)));
        return result;
    }

    @Override
    public ResponseResult deletePushNotice(Long id) {
        TPushNotice pushNotice = pushNoticeMapper.selectById(id);
        if (null == pushNotice) {
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).msg("未查找到该推送通知").build();
        }
        int i = pushNotice.getStartTime().compareTo(new Date());
        int j = pushNotice.getEndTime().compareTo(new Date());
        if (i > 0 && pushNotice.getStatus() == 1) {
            //当前时间小于开始时间 并且推送状态为未开始（有可能还在异步调用发送推送所以要做判断），调用极光推送官方API删除接口
            int deleteResult = JPushUtil.DeleteSchedule(pushNotice.getScheduleId());
            if (1 != deleteResult) {
                return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).msg("极光推送删除失败").build();
            }
        }
        if (j < 0) {
            //当前时间大于结束时间，已经是下线状态，不能再次下线
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).msg("当前推送通知已失效，下线失败").build();
        }
        //修改结束时间
        pushNotice.setEndTime(new Date());
        //修改为下线
        pushNotice.setOnlineStatus(0);
        //修改为已失效
        pushNotice.setStatus(3);
        Integer updateResult = pushNoticeMapper.updateById(pushNotice);
        if (0 == updateResult) {
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).msg("更新结束时间失败").build();
        }
        return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).build();
    }

    /**
     * 根据uid数组获取不重复的jid列表
     */
    private List<String> getJidListWithUid(String uids) {
        String[] uidStr = uids.split(",");
        List<String> jidList = new ArrayList<>();
        for (String s : uidStr) {
            Long uid = Long.valueOf(s);
            TUserInfo tUserInfo = userInfoMapper.selectById(uid);
            if (null != tUserInfo.getJid() && !"".equals(tUserInfo.getJid())) {
                jidList.add(tUserInfo.getJid());
            }
        }
        return getDistinctList(jidList);
    }

    /**
     * 根据节点数组获取不重复的jid列表
     *      传参只是一级节点，根据一级节点要获取其所有子节点
     */
    private List<String> getJidListWithNodeId(String nodeIds) {
        String[] nodeIdsStr = nodeIds.split(",");
        List<Long> nodeIdsList = new ArrayList<>();
        for (String s : nodeIdsStr) {
            Long nodeId = Long.valueOf(s);
            nodeIdsList.add(nodeId);
            List<Long> childNodeIdList = getChildNodeId(nodeId);
            if (null != childNodeIdList) {
                nodeIdsList.addAll(childNodeIdList);
            }
        }
        List<TUserInfo> userInfoList = userInfoMapper.selectList(new EntityWrapper<TUserInfo>().in("server_node_id", nodeIdsList));
        List<String> jidList = new ArrayList<>();
        for (TUserInfo tUserInfo : userInfoList) {
            if (null != tUserInfo.getJid() && !"".equals(tUserInfo.getJid())) {
                jidList.add(tUserInfo.getJid());
            }
        }
        return getDistinctList(jidList);
    }

    /**
     * 根据一级节点id获取其子节点（二级+三级）的id列表
     */
    private List<Long> getChildNodeId(Long nodeId) {
        TServerNode tServerNode = serverNodeMapper.selectById(nodeId);
        if (0 == tServerNode.getChildStatus()) {
            return null;
        }
        Long[] childList = (Long[]) ConvertUtils.convert(tServerNode.getChildIds().split(","),Long.class);
        List<Long> list = Arrays.stream(childList).collect(Collectors.toList());
        list.removeIf(nodeId::equals);
        return list;
    }

    /**
     * 去除String类型list中重复的元素
     */
    private List<String> getDistinctList(List<String> list) {
        List<String> newList = new ArrayList<>();
        for (String s : list) {
            if (!newList.contains(s)) {
                newList.add(s);
            }
        }
        return newList;
    }
}
