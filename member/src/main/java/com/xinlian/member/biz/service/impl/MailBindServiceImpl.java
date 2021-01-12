package com.xinlian.member.biz.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.xinlian.biz.dao.TServerNodeMapper;
import com.xinlian.biz.dao.TUserInfoMapper;
import com.xinlian.biz.model.TServerNode;
import com.xinlian.biz.model.TUserInfo;
import com.xinlian.biz.utils.NodeVoyageUtil;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.enums.MailTemplateEnum;
import com.xinlian.common.request.BindMailReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.redis.RedisConstant;
import com.xinlian.member.biz.service.MailBindService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class MailBindServiceImpl implements MailBindService {
    @Autowired
    private TUserInfoMapper tUserInfoMapper;
    @Autowired
    private TServerNodeMapper tServerNodeMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private NodeVoyageUtil nodeVoyageUtil;

    @Override
    public int findEmailExists(String email,Long nodeId) {
        TServerNode tServerNode = tServerNodeMapper.selectById(nodeId);
        int emailCount = tServerNode.getEmailBindAmount();
        //0：不做限制，非0：不超过填写的数字。当节点属于大航海计划的，则此字段含义为当前节点绑定的未激活账号数量限制。
        //判断节点是否属于在大航海节点
        if (nodeVoyageUtil.belongVoyageNode(nodeId)) {
            Integer count = tUserInfoMapper.getNodeEmailStayActivateNumber(nodeId,email);
            //0：符合绑定要求，1：国内用户绑定不合规时的情况，2：大航海用户绑定不合规时的情况
            return count < emailCount || 0 == emailCount ? 0 : 2;
        }else {
            Integer count = tUserInfoMapper.selectCount(new EntityWrapper<TUserInfo>().eq("email", email).eq("server_node_id",nodeId));
            return count < emailCount || 0 == emailCount ? 0 : 1;
        }
    }

    @Override
    public ResponseResult bindMail(BindMailReq bindMailReq) {
        String emailCode = redisClient.get(RedisConstant.EMAIL_CODE_KEY_PREFIX + bindMailReq.getEmail() + "_" + MailTemplateEnum.BIND_TYPE.getCode());
        if(null == emailCode ||!bindMailReq.getCode().equals(emailCode)){
            log.error("redis中的value值:"+emailCode+"接收到的参数code"+bindMailReq.getCode());
            throw new BizException("输入验证码不正确或验证码已过期!");
        }
        TUserInfo userInfo = tUserInfoMapper.selectById(bindMailReq.getUid());
        if (null == userInfo) {
            throw new BizException("未获取到用户信息！");
        }
        if (userInfo.getEmail() != null && !"".equals(userInfo.getEmail())) {
            log.error("绑定邮箱的用户id：" + bindMailReq.getUid());
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).msg("账号已绑定了邮箱，请勿重复绑定！").build();
        }
        TUserInfo tUserInfo = new TUserInfo();
        tUserInfo.setUid(bindMailReq.getUid());
        tUserInfo.setEmail(bindMailReq.getEmail());
        Integer updateResult = tUserInfoMapper.updateById(tUserInfo);
        if (0 == updateResult) {
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).msg("绑定邮箱失败").build();
        }
        return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).build();
    }
}
