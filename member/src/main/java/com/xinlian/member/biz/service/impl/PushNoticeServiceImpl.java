package com.xinlian.member.biz.service.impl;

import com.xinlian.biz.dao.TPushNoticeMapper;
import com.xinlian.biz.dao.TServerNodeMapper;
import com.xinlian.biz.dao.TUserInfoMapper;
import com.xinlian.biz.model.AppNoticePushRecordModel;
import com.xinlian.biz.model.TPushNotice;
import com.xinlian.biz.model.TServerNode;
import com.xinlian.biz.model.TUserInfo;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.enums.JPushTitleMessageEnum;
import com.xinlian.common.request.PushNoticeReq;
import com.xinlian.common.response.PushNoticeRes;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.utils.JPushUtil;
import com.xinlian.member.biz.service.PushNoticeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PushNoticeServiceImpl implements PushNoticeService {
    @Autowired
    private TPushNoticeMapper pushNoticeMapper;
    @Autowired
    private TServerNodeMapper serverNodeMapper;
    @Autowired
    private TUserInfoMapper userInfoMapper;

    @Override
    public ResponseResult findNoticeEndTime(PushNoticeReq pushNoticeReq) {
        TPushNotice pushNotice = new TPushNotice();
        pushNotice.setUniqueCode(pushNoticeReq.getUniqueCode());
        TPushNotice tPushNotice = pushNoticeMapper.selectOne(pushNotice);
        if (null == tPushNotice) {
            //移动端不需要让用户看到错误的提示信息，msg不填写
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).build();
        }
        PushNoticeRes pushNoticeRes = new PushNoticeRes();
        pushNoticeRes.setUniqueCode(pushNoticeReq.getUniqueCode());
        pushNoticeRes.setEndTime(tPushNotice.getEndTime());
        if (null != tPushNotice.getUids() && !"".equals(tPushNotice.getUids())) {
            //根据uid推送给特定用户
            Long[] uidS = (Long[]) ConvertUtils.convert(tPushNotice.getUids().split(","),Long.class);
            List<Long> list = Arrays.stream(uidS).collect(Collectors.toList());
            pushNoticeRes.setUidList(list);
        }
        if (null != tPushNotice.getNodeIds() && !"".equals(tPushNotice.getNodeIds())) {
            //根据节点id推送给特定用户
            Long[] nodeIds = (Long[]) ConvertUtils.convert(tPushNotice.getNodeIds().split(","),Long.class);
            List<Long> list = Arrays.stream(nodeIds).collect(Collectors.toList());
            List<Long> childNodeIdList = new ArrayList<>();
            for (Long nodeId : list) {
                List<Long> childIdList = getChildNodeIdList(nodeId);
                if (null != childIdList) {
                    childNodeIdList.addAll(childIdList);
                }
            }
            list.addAll(childNodeIdList);
            pushNoticeRes.setNodeIdList(list);
        }
        return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).result(pushNoticeRes).build();
    }

    /**
     * 根据一级节点id获取其子节点（二级+三级）的id列表
     */
    private List<Long> getChildNodeIdList(Long nodeId) {
        TServerNode tServerNode = serverNodeMapper.selectById(nodeId);
        if (0 == tServerNode.getChildStatus()) {
            return null;
        }
        Long[] childList = (Long[]) ConvertUtils.convert(tServerNode.getChildIds().split(","),Long.class);
        List<Long> list = Arrays.stream(childList).collect(Collectors.toList());
        list.removeIf(nodeId::equals);
        return list;
    }

    @Override
    public void saveAppNoticePushRecord(Long uid, String jid,String currencyCode, String amount,JPushTitleMessageEnum jPushTitleMessageEnum){
        String replaceMsg = amount + currencyCode;
        JPushTitleMessageEnum.getReplaceMsg(jPushTitleMessageEnum.getMsg(), replaceMsg);
        if(StringUtils.isEmpty(jid)){
            TUserInfo userInfo = userInfoMapper.selectById(uid);
            jid = userInfo.getJid();
        }
        String pushContent = JPushTitleMessageEnum.getReplaceMsg(jPushTitleMessageEnum.getMsg(), replaceMsg);
        newThreadSavePushModel(uid,jid,pushContent,jPushTitleMessageEnum);
    }

    private void newThreadSavePushModel(Long uid, String jid,String pushContent,JPushTitleMessageEnum jPushTitleMessageEnum){
        new Thread(()->{
            //调用三方推送通知
            int pushResult = 0;
            try {
                pushResult = JPushUtil.sendToRegistrationId(jid,
                        pushContent,
                        jPushTitleMessageEnum.getTitle());
                if (pushResult == 1) {
                    log.info("极光通知成功");
                } else {
                    log.info("极光通知失败，用户id：" + uid);
                }
            } catch (Exception e) {
                log.error("极光通知出现异常:{}",e.toString(),e);
            }
            //组装推送记录model
            AppNoticePushRecordModel noticePushRecordModel = new AppNoticePushRecordModel();
            noticePushRecordModel.setPushTitle(jPushTitleMessageEnum.getTitle());
            noticePushRecordModel.setPushContent(pushContent);
            noticePushRecordModel.setPushUid(uid);
            noticePushRecordModel.setPushJid(jid);
            noticePushRecordModel.setPushResult(pushResult);
            pushNoticeMapper.appNoticePushRecord(noticePushRecordModel);
        }).start();
    }



}
