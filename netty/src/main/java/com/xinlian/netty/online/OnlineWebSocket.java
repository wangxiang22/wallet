package com.xinlian.netty.online;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.xinlian.common.utils.CommonUtil;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.yeauty.annotation.*;
import org.yeauty.pojo.Session;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * <p>
 * 使用netty实现的websocket记录用户在线时长
 * </p>
 */

@ServerEndpoint(path = "/ws/online", port = "11211")
@Slf4j
@Data
public class OnlineWebSocket {
    /***
     * session链接
     */
    private Session session;

    /**
     * 最后活跃时间
     */
    private long activeTime;

    private int cumulativeDuration;
    private long onlineTime;
    private long offlineTime;
    private String uid;


    @OnOpen
    public void onOpen(Session session) {
        log.info("新的链接加入");
        this.activeTime = CommonUtil.timeStamp();
        this.session = session;
        OnlineConnectionsManager.getConnectionSet().add(this);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        log.info("一个链接关闭了");
        calcCumulativeDuration();
    }

    private void calcCumulativeDuration() {
        if (StringUtils.isNotBlank(this.uid) && this.onlineTime != 0) {
            this.offlineTime = System.currentTimeMillis();
            int timeDiff = (int) (this.offlineTime - this.onlineTime);
            OnlineConnectionsManager.getCumulativeDurationMap()
                    .put(uid, OnlineConnectionsManager.getCumulativeDurationMap().get(uid) + timeDiff);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.info("链接发生异常");
        calcCumulativeDuration();
    }

    /**
     * 群发自定义消息
     */
    public static void sendInfo(WebSocketData data) {
        CopyOnWriteArraySet<OnlineWebSocket> socketSet = OnlineConnectionsManager.getConnectionSet();
        if (socketSet != null) {
            socketSet.parallelStream().forEach(item -> {
                if (item != null && item.getSession().isOpen()) {
                    item.sendMessage(data);
                }
            });
        }
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        //更新活跃时间
        this.activeTime = CommonUtil.timeStamp();
        //错误消息
        if (StringUtils.isEmpty(message.trim())) {
            session.sendText(JSON.toJSONString(WebSocketData.builder()
                    .ts(this.activeTime).ch("error").data("消息不能为空").build(), SerializerFeature.WriteMapNullValue));
            return;
        }
        //心跳消息
        if (StringUtils.equals(message, "ping")) {
            calcCumulativeDuration();
            if (StringUtils.isNotEmpty(this.uid)) {
                this.onlineTime = System.currentTimeMillis();
            }
            session.sendText(JSON.toJSONString(WebSocketData.builder()
                    .ts(this.activeTime).ch("success").data("pong").build(), SerializerFeature.WriteMapNullValue));
            return;
        }
        //订阅取消消息
        try {
            //{"op": "sub","uid": "123"}
            JSONObject object = JSON.parseObject(message);
            String op = object.getString("op");
            String uid = object.getString("uid");
            this.uid = uid;

            if (StringUtils.equals(op, "sub")) {//订阅消息

                this.onlineTime = System.currentTimeMillis();
                OnlineConnectionsManager.getCumulativeDurationMap().putIfAbsent(uid, 0);

                WebSocketData data = WebSocketData.builder().ts(CommonUtil.timeStamp()).data("订阅成功").ch("success").build();
                sendMessage(data);
            } else if (StringUtils.equals(op, "unsub")) {//取消订阅消息

                WebSocketData data = WebSocketData.builder().ts(CommonUtil.timeStamp()).data("取消订阅成功").ch("success").build();
                sendMessage(data);
                this.session.close();
                OnlineConnectionsManager.getConnectionSet().remove(this);
            }
        } catch (Exception e) {
            session.sendText(JSON.toJSONString(WebSocketData.builder()
                    .ts(this.activeTime).ch("error").data("消息内容有误").build(), SerializerFeature.WriteMapNullValue));
        }
    }

    private void manualFinishTodayTime(long activeTime) {
        // 判断当前在线时间与下日0点时间相差不足三十秒,更新在线时间
        if (CommonUtil.getTheDayResidueSecond() - activeTime < 30) {
            calcCumulativeDuration();
        }
    }

    /**
     * 发生自定义消息
     *
     * @param data 消息
     */
    private synchronized void sendMessage(WebSocketData data) {
        if (this.session.isOpen() && this.session.isActive()) {
            try {
                this.session.sendText(JSONObject.toJSONString(data, SerializerFeature.WriteMapNullValue));
            } catch (Exception e) {
                log.warn("推送信息失败，连接已经关闭");
            }
        }
    }

    @OnBinary
    public void onBinary(Session session, byte[] bytes) {
        for (byte b : bytes) {
            System.out.println(b);
        }
        session.sendBinary(bytes);
    }

    @OnEvent
    public void onEvent(Session session, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            switch (idleStateEvent.state()) {
                case READER_IDLE:
                    System.out.println("read idle");
                    break;
                case WRITER_IDLE:
                    System.out.println("write idle");
                    break;
                case ALL_IDLE:
                    System.out.println("all idle");
                    break;
                default:
                    break;
            }
        }
    }

}