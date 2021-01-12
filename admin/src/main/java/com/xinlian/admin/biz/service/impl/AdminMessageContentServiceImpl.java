package com.xinlian.admin.biz.service.impl;

import com.xinlian.admin.biz.redis.RedisClient;
import com.xinlian.admin.biz.service.AdminMessageContentService;
import com.xinlian.biz.dao.AdminMessageContentMapper;
import com.xinlian.biz.dao.AdminReceiveMessageMapper;
import com.xinlian.biz.dao.TUserInfoMapper;
import com.xinlian.biz.model.AdminMessageContent;
import com.xinlian.biz.model.AdminReceiveMessage;
import com.xinlian.biz.utils.CommonRedisClient;
import com.xinlian.common.enums.AdminMessageContentEnum;
import com.xinlian.common.enums.AdminReceiveMessageEnum;
import com.xinlian.common.request.MessageContentReceiveReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;

/**
 * <p>
 * 消息内容表 服务实现类
 * </p>
 *
 * @author lt
 * @since 2020-06-13
 */
@Service
public class AdminMessageContentServiceImpl implements AdminMessageContentService {
    @Autowired
    private AdminMessageContentMapper adminMessageContentMapper;
    @Autowired
    private AdminReceiveMessageMapper adminReceiveMessageMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private TUserInfoMapper userInfoMapper;
    @Autowired
    private CommonRedisClient commonRedisClient;

    @Override
    @Transactional
    public ResponseResult createMessageContentReceive(MessageContentReceiveReq req) {
        AdminMessageContent messageContent = new AdminMessageContent();
        messageContent.setTitle(req.getTitle());
        messageContent.setContent(req.getContent());
        messageContent.setTypeCode(req.getTypeCode());
        if (AdminMessageContentEnum.NEWS_MESSAGE.getTypeCode().equals(req.getTypeCode())) {
            messageContent.setThumbnail(req.getThumbnail());
            messageContent.setHyperlink(req.getHyperlink());
        }
        Integer insertMsgResult = adminMessageContentMapper.insert(messageContent);
        if (0 >= insertMsgResult) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new BizException("新增消息内容失败");
        }
        //新增用户消息的话，只是模板，不添加到用户接收消息表中
        if (AdminMessageContentEnum.USER_MESSAGE.getTypeCode().equals(req.getTypeCode())) {
            return ResponseResult.ok();
        }
        //新增活动消息及系统消息，则需要在接收消息表中插入数据
        AdminReceiveMessage receiveMessage = new AdminReceiveMessage();
        receiveMessage.setRoleType(req.getRoleType());
        receiveMessage.setMessageId(messageContent.getId());
        //消息接收方如果是全部用户，则uids字段不用填写，反之要填写
        if (AdminReceiveMessageEnum.ONE_USER.getRoleTypeCode().equals(req.getRoleType()) || (AdminReceiveMessageEnum.USER_GROUPS.getRoleTypeCode().equals(req.getRoleType()))) {
            receiveMessage.setUids(req.getUids());
        }
        Integer insertReceiveResult = adminReceiveMessageMapper.insert(receiveMessage);
        if (0 >= insertReceiveResult) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new BizException("新增用户接收消息数据失败");
        }
        //消息接收表插入成功后需要给对应用户的对应消息设置未读状态
        //redis中存入未读消息id到对应key中
        //发送给全部用户的情况
        if (AdminReceiveMessageEnum.ALL_USER.getRoleTypeCode().equals(req.getRoleType())) {
            //获取所有用户uid
            List<String> userIdAll = userInfoMapper.findUserIdAll();



        }
        return ResponseResult.ok();
    }
}