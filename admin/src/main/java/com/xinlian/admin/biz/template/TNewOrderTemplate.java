package com.xinlian.admin.biz.template;

import com.xinlian.biz.dao.TUserInfoMapper;
import com.xinlian.biz.model.TNewOrder;
import com.xinlian.biz.model.TUserInfo;
import com.xinlian.common.utils.JPushUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class TNewOrderTemplate {
    @Autowired
    private TUserInfoMapper tUserInfoMapper;

    @Async
    public void publishMessage(TNewOrder tNewOrder) {
        //获取当前用户的设备id
        TUserInfo userInfo = tUserInfoMapper.selectById(tNewOrder.getUid());
        String message = "尊敬的" + tNewOrder.getUserName() + "，您好：您的商品已发货，物流公司："
                + tNewOrder.getExpressName() + "，快递单号：" + tNewOrder.getExpressCode()
                + "请及时关注物流信息，祝您购物愉快";
        if(StringUtils.isNotBlank(userInfo.getJid())){
            JPushUtil.sendToRegistrationId(userInfo.getJid(), message, "发货提醒");
        }
    }
}
