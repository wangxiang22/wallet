package com.xinlian.netty.online;

import com.alibaba.fastjson.JSON;
import com.xinlian.common.utils.CommonUtil;

import java.util.concurrent.*;


public class OnlineConnectionsManager {

    private static final int TIMEOUT = 60;//60秒不发心跳就终结

    //处理过期/失效的链接
    static {
        final ScheduledExecutorService service = Executors
                .newSingleThreadScheduledExecutor();
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        service.scheduleAtFixedRate(() -> {
            long time = CommonUtil.timeStamp();
//            sendMessage(time);
            listenerAlive(time);
        }, 1, 10, TimeUnit.SECONDS);
    }

    private static void sendMessage(long time) {
        WebSocketData data = WebSocketData.builder().ts(time).ch("success").build();
        OnlineWebSocket.sendInfo(data);
    }

    private static void listenerAlive(long time) {
        getConnectionSet().forEach(item -> {
            if (time - item.getActiveTime() > TIMEOUT || !(item.getSession().isActive() && item.getSession().isOpen())) {
                WebSocketData data = WebSocketData.builder().ts(time).ch("您已经断开连接，请重新登录！").build();
                item.getSession().sendText(JSON.toJSONString(data));
                item.getSession().close();
                getConnectionSet().remove(item);
            }
        });
    }

    /**
     * 用户当天在线时长保存在内存中,key - uid ,value - ms
     */
    private static ConcurrentHashMap<String, Integer> cumulativeDurationMap = new ConcurrentHashMap<>();

    private static CopyOnWriteArraySet<OnlineWebSocket> connectionSet = new CopyOnWriteArraySet<>();

    public static ConcurrentHashMap<String, Integer> getCumulativeDurationMap() {
        return cumulativeDurationMap;
    }

    public static CopyOnWriteArraySet<OnlineWebSocket> getConnectionSet() {
        return connectionSet;
    }

    public static void setConnectionMap(CopyOnWriteArraySet<OnlineWebSocket> connectionSet) {
        OnlineConnectionsManager.connectionSet = connectionSet;
    }

    public static void clearCumulativeDurationMap() {
        cumulativeDurationMap = new ConcurrentHashMap<>();
    }
}
