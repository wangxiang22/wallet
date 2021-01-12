package com.xinlian.admin.biz.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.xinlian.admin.biz.redis.RedisClient;
import com.xinlian.admin.biz.redis.RedisConstant;
import com.xinlian.admin.biz.service.UserAuthManagerService;
import com.xinlian.admin.biz.utils.BaiduAdvancedGeneral;
import com.xinlian.admin.biz.utils.BaiduAuthService;
import com.xinlian.biz.dao.TServerNodeMapper;
import com.xinlian.biz.dao.TUserAuthMapper;
import com.xinlian.biz.dao.TUserInfoMapper;
import com.xinlian.biz.model.TServerNode;
import com.xinlian.biz.model.TUserAuth;
import com.xinlian.biz.model.TUserInfo;
import com.xinlian.biz.utils.AdminOptionsUtil;
import com.xinlian.biz.utils.NodeVoyageUtil;
import com.xinlian.common.dto.UserUidAuthSnDto;
import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
import com.xinlian.common.enums.CommonEnum;
import com.xinlian.common.enums.ErrorCode;
import com.xinlian.common.request.RefuseReq;
import com.xinlian.common.request.UserAuthQueryReq;
import com.xinlian.common.request.UserAuthUpdateReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.response.SystemSwitchRes;
import com.xinlian.common.result.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class UserAuthManagerServiceImpl implements UserAuthManagerService {
    @Autowired
    private TUserAuthMapper tUserAuthMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private AdminOptionsUtil adminOptionsUtil;
    @Autowired
    private NodeVoyageUtil nodeVoyageUtil;
    @Autowired
    private TUserInfoMapper userInfoMapper;
    @Autowired
    private BaiduAdvancedGeneral baiduAdvancedGeneral;
    @Autowired
    private TServerNodeMapper serverNodeMapper;

    final Executor executor = Executors.newFixedThreadPool(6);

    @Override
    public ResponseResult queryAll(UserAuthQueryReq userAuthQueryReq) throws Exception{
        if (userAuthQueryReq.getPageNum()!=null && userAuthQueryReq.getPageNum2()!=null) {
            userAuthQueryReq.setPageNum((userAuthQueryReq.getPageNum() - 1) * userAuthQueryReq.getPageNum2());
        }else{
            userAuthQueryReq.setPageNum(0L);
            userAuthQueryReq.setPageNum2(10L);
        }
        if(this.checkRequestIsBothNull(userAuthQueryReq)){
            Long nowTime = System.currentTimeMillis()/1000;
            userAuthQueryReq.setEndTime(nowTime);
            userAuthQueryReq.setStartTime(nowTime-30*24*60*60);
            userAuthQueryReq.setStatus(1);
        }
        CompletableFuture<List<TUserAuth>> future1 =  CompletableFuture.supplyAsync(()->tUserAuthMapper.queryAll(userAuthQueryReq),executor);
        CompletableFuture<Integer> future2 =  CompletableFuture.supplyAsync(()->getCount(userAuthQueryReq),executor);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("list",future1.get());
        jsonObject.put("count",future2.get());
        return ResponseResult.builder().msg(ErrorCode.REQ_SUCCESS.getDes()).code(ErrorCode.REQ_SUCCESS.getCode()).result(jsonObject).build();
    }

    private Integer getCount(UserAuthQueryReq userAuthQueryReq) {
        try{
            String redisKey = RedisConstant.ADMIN_REDIS_PREFIX.concat("PAGE_").concat(this.getMd5ByRequestParam(userAuthQueryReq));
            log.info("User auth redis count(0) key:{}", redisKey);
            Integer count = redisClient.get(redisKey);
            if (null == count) {
                count = tUserAuthMapper.queryCount(userAuthQueryReq);
                redisClient.set(redisKey, count, 15 * 60L);
            }
            return count;
        }catch (Exception e){
            return 0;
        }
    }

    //check request is all null
    private boolean checkRequestIsBothNull(UserAuthQueryReq userAuthQueryReq)throws Exception{
        boolean checkBothNull = true;
        //通过反射赋值
        for(Field field : userAuthQueryReq.getClass().getDeclaredFields()){
            if(field.getName().equalsIgnoreCase("pageNum")
                    ||field.getName().equalsIgnoreCase("pageNum2")) {
                continue;
            }
            field.setAccessible(true);
            Object obj = field.get(userAuthQueryReq);
            if(!StringUtils.isEmpty(obj)){
                checkBothNull = false;
                break;
            }
        }
        return checkBothNull;
    }

    private String getMd5ByRequestParam(UserAuthQueryReq userAuthQueryReq)throws Exception{
        StringBuffer stayMd5Char = new StringBuffer();
        for(Field field : userAuthQueryReq.getClass().getDeclaredFields()){
            if(field.getName().equalsIgnoreCase("pageNum")
                    ||field.getName().equalsIgnoreCase("pageNum2")){
                continue;
            }
            field.setAccessible(true);
            Object obj = field.get(userAuthQueryReq);
            stayMd5Char.append(field.getName()).append("=").append(obj).append("&");
        }
        log.info("实名审核stayMd5Char:{}",stayMd5Char);
        return DigestUtils.md5Hex(stayMd5Char.toString());
    }


    @Override
    public ResponseResult takeOffer(List<Long> list) {
        //合规uid集合
        List<Long> passUidList = new ArrayList<>();
        //不合规uid集合
        List<Long> refuseUidList = new ArrayList<>();
        for (Long uid : list) {
            if (eligibleUid(uid)){
                passUidList.add(uid);
            }else {
                refuseUidList.add(uid);
            }
        }
        if (passUidList.size() == 0) {
            return ResponseResult.builder().msg("不合规uid列表："+refuseUidList).code(ErrorCode.REQ_SUCCESS.getCode()).build();
        }
        Integer count = tUserAuthMapper.takeOffer(passUidList);
        return ResponseResult.builder().msg(CommonEnum.UPDATE_OK.getDes()+"！不合规uid列表："+refuseUidList).code(ErrorCode.REQ_SUCCESS.getCode()).result(count).build();
    }

    @Override
    public ResponseResult refuse(List<Long> list) {
        Integer count = tUserAuthMapper.refuse(list);
        return ResponseResult.builder().msg(CommonEnum.UPDATE_OK.getDes()).code(ErrorCode.REQ_SUCCESS.getCode()).result(count).build();
    }

    @Override
    public ResponseResult updateByUid(UserAuthUpdateReq userAuthUpdateReq) {
        Integer count = tUserAuthMapper.updateByUid(userAuthUpdateReq);
        return ResponseResult.builder().msg(CommonEnum.UPDATE_OK.getDes()).code(ErrorCode.REQ_SUCCESS.getCode()).result(count).build();
    }

    @Override
    public ResponseResult refuseOne(RefuseReq refuseReq) {
        TUserAuth tUserAuth = new TUserAuth();
        tUserAuth.setStatus(2);
        tUserAuth.setNote(refuseReq.getRefuseReason());
        tUserAuthMapper.update(tUserAuth,new EntityWrapper<TUserAuth>().eq("uid",refuseReq.getUid()));
        return ResponseResult.builder().msg(CommonEnum.UPDATE_OK.getDes()).code(ErrorCode.REQ_SUCCESS.getCode()).result(null).build();
    }

    @Override
    public void batchAuditByBaiduRecognition() {
        List<UserUidAuthSnDto> uidAuthSnDtoList = tUserAuthMapper.findUidListByStatus();
        if (null == uidAuthSnDtoList || uidAuthSnDtoList.size() == 0) {
            return;
        }
        this.batchAudit(uidAuthSnDtoList);
    }

    @Async
    public void batchAudit(List<UserUidAuthSnDto> uidAuthSnDtoList) {
        List<UserUidAuthSnDto> dtoList = new ArrayList<>();
        for (UserUidAuthSnDto uidAuthSnDto : uidAuthSnDtoList) {
            //先判断各种开关和数量的限制是否符合
            if (this.eligibleUid(uidAuthSnDto)) {
                //再判断手持身份证照片是否合规
                dtoList.add(uidAuthSnDto);
            }
        }
        //new list
        for (UserUidAuthSnDto dto : dtoList) {
            if (this.baiduRecognition(dto.getAuthScsfz())) {
                tUserAuthMapper.updateByStatus(dto.getUid());
            }
        }
    }

    private boolean eligibleUid(Long uid) {
        //针对矿机已经激活，但是实名待审核及实名认证被驳回的会员需要重新认证的情况。这样的会员不限制实名次数和年龄。
        List<Long> activeNotAuthList = tUserAuthMapper.findActiveNotAuth(uid);
        if (null == activeNotAuthList || activeNotAuthList.size() == 0 || !activeNotAuthList.contains(uid)) {
            //判断全局开关是否打开，开了则走全局配置，没开就走节点单独配置
            try {
                SystemSwitchRes systemSwitchRes =
                        adminOptionsUtil.fieldEntityObject(AdminOptionsBelongsSystemCodeEnum.SYSTEM_SWITCH.getBelongsSystemCode(), SystemSwitchRes.class);
                //全局开关不包含对大航海及其子节点的控制
                TUserInfo tUserInfo = new TUserInfo();
                tUserInfo.setUid(uid);
                TUserInfo userInfo = userInfoMapper.selectOne(tUserInfo);
                //获取用户实名申请信息
                TUserAuth tUserAuth = new TUserAuth();
                tUserAuth.setUid(uid);
                TUserAuth userAuth = tUserAuthMapper.selectOne(tUserAuth);
                //获取用户所在节点信息
                TServerNode queryResult = this.getById(userInfo.getServerNodeId());
                //全局开关打开并且不是大航海节点的用户
                if (null != systemSwitchRes && "1".equals(systemSwitchRes.getSystemApplicationFlag()) && !nodeVoyageUtil.belongVoyageNode(userInfo.getServerNodeId())) {
                    Integer count = tUserAuthMapper.getUserAuthingCount(userAuth.getAuthSn());
                    if (count > Integer.parseInt(systemSwitchRes.getAuthRegisterAmount())) {
                        return false;
                    }
                } else {
                    //亚历山大判断
                    if (userInfo.getServerNodeId() == 110L) {
                        Integer notBelongAlexandriaCount = tUserAuthMapper.getNotBelongAlexandriaCount(userAuth.getAuthSn());
                        //查看身份证号是否在别的节点正在审核或已通过审核，如是则实名驳回
                        if (notBelongAlexandriaCount > 0) {
                            return false;
                        }
                    }
                    //同节点同身份证号实名个数不得超过该节点的配置，如果节点配置是0则表示不限制数量
                    Integer sameNodeAuthCount = tUserAuthMapper.getSameNodeAuthCount(userAuth.getAuthSn(), userInfo.getServerNodeId());
                    if (sameNodeAuthCount >= queryResult.getAuthRegisterAmount() && queryResult.getAuthRegisterAmount() != 0) {
                        return false;
                    }
                }
            }catch (Exception e){
                log.error("查找全局配置出现异常：{}",e.toString(),e);
                return false;
            }
        }
        return true;
    }

    private boolean eligibleUid(UserUidAuthSnDto uidAuthSnDto) {
        //针对矿机已经激活，但是没有提交实名认证及实名认证被驳回的会员需要重新认证的情况。这样的会员不限制实名次数和年龄。
        List<Long> activeNotAuthList = tUserAuthMapper.findActiveNotAuth(uidAuthSnDto.getUid());
        if (null == activeNotAuthList || activeNotAuthList.size() == 0 || !activeNotAuthList.contains(uidAuthSnDto.getUid())) {
            //判断全局开关是否打开，开了则走全局配置，没开就走节点单独配置
            try {
                SystemSwitchRes systemSwitchRes =
                        adminOptionsUtil.fieldEntityObject(AdminOptionsBelongsSystemCodeEnum.SYSTEM_SWITCH.getBelongsSystemCode(), SystemSwitchRes.class);
                //全局开关不包含对大航海及其子节点的控制
                TUserInfo tUserInfo = new TUserInfo();
                tUserInfo.setUid(uidAuthSnDto.getUid());
                TUserInfo userInfo = userInfoMapper.selectOne(tUserInfo);
                //获取用户所在节点信息
                TServerNode queryResult = this.getById(userInfo.getServerNodeId());
                //全局开关打开并且不是大航海节点的用户
                if (null != systemSwitchRes && "1".equals(systemSwitchRes.getSystemApplicationFlag()) && !nodeVoyageUtil.belongVoyageNode(userInfo.getServerNodeId())) {
                    Integer count = tUserAuthMapper.getUserAuthingCount(uidAuthSnDto.getAuthSn());
                    if (count > Integer.parseInt(systemSwitchRes.getAuthRegisterAmount())) {
                        return false;
                    }
                } else {
                    //亚历山大判断
                    if (userInfo.getServerNodeId() == 110L) {
                        Integer notBelongAlexandriaCount = tUserAuthMapper.getNotBelongAlexandriaCount(uidAuthSnDto.getAuthSn());
                        //查看身份证号是否在别的节点正在审核或已通过审核，如是则实名驳回
                        if (notBelongAlexandriaCount > 0) {
                            return false;
                        }
                    }
                    //同节点同身份证号实名个数不得超过该节点的配置，如果节点配置是0则表示不限制数量
                    Integer sameNodeAuthCount = tUserAuthMapper.getSameNodeAuthCount(uidAuthSnDto.getAuthSn(), userInfo.getServerNodeId());
                    if (sameNodeAuthCount >= queryResult.getAuthRegisterAmount() && queryResult.getAuthRegisterAmount() != 0) {
                        return false;
                    }
                }
            }catch (Exception e){
                log.error("查找全局配置出现异常：{}",e.toString(),e);
                return false;
            }
        }
        return true;
    }

    public TServerNode getById(@NotNull Long serverNodeId) {
        if (null == serverNodeId) {
            return null;
        }
        try {
            String serverNodeSuffix = "WITHDRAW_SERVER_NODE" + serverNodeId;
            String redisKey = RedisConstant.APP_REDIS_PREFIX + serverNodeSuffix;
            String redisValue = redisClient.get(redisKey);
            if (org.apache.commons.lang3.StringUtils.isBlank(redisValue)) {
                TServerNode serverNode = serverNodeMapper.selectById(serverNodeId);
                if (null != serverNode) {
                    redisClient.set(redisKey, JSONObject.toJSONString(serverNode));
                }
                return serverNode;
            } else {
                return JSONObject.parseObject(redisValue, TServerNode.class);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * 百度图像识别，判断手持身份证照片是否合规
     * @param filePath 手持身份证照片oss地址
     * @return 结果：true：合规，false：不合规
     */
    private boolean baiduRecognition(String filePath) {
        String advancedGeneral = baiduAdvancedGeneral.advancedGeneral(filePath);
        if (null == advancedGeneral) {
            return false;
        }
        JSONObject jsonObject = JSONObject.parseObject(advancedGeneral);
        JSONArray result = jsonObject.getJSONArray("result");
        if (result.size() == 0) {
            return false;
        }
        int rootCount = 0;
        int keywordCount = 0;
        for (int i = 0; i < result.size(); i++) {
            if ("人物-人物特写".equals(result.getJSONObject(i).get("root"))) {
                rootCount++;
            }
            if (((String)result.getJSONObject(i).get("keyword")).contains("身份证") || "个人证件".equals(result.getJSONObject(i).get("keyword"))) {
                keywordCount++;
            }
        }
        return rootCount > 0 && keywordCount > 0;
    }
}
