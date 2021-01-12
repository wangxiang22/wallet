package com.xinlian.common.utils;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import cn.jpush.api.schedule.ScheduleResult;
import cn.jpush.api.schedule.model.SchedulePayload;
import cn.jpush.api.schedule.model.TriggerPayload;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.enums.JPushTitleMessageEnum;
import com.xinlian.common.response.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 极光推送
 *      只用定时任务，不用定期任务
 */
public class JPushUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JPushUtil.class);
    private final static Boolean ApnsProduction = false;//上线之后要改为true

    private final static String appKey = "c76635bbca05a32b74e57025";
    private final static String masterSecret = "c427bd72ed2253a4d0ab8bd6";

//    public static void main(String[] args) throws ParseException {
//        List<String> list = new ArrayList<>();
//        list.add("120c83f760bd89fd448");
//        Map<String,String> map = new HashMap<>();
//        map.put("title","推送标题测试");
//        map.put("type","2");
//        map.put("closeStatus","1");
//        map.put("uuid","uuid123");
//        map.put("text","测试文本");
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date start = format.parse("2020-04-01 16:10:00");
//        Date end = format.parse("2020-04-01 16:15:00");
//        ResponseResult result = JPushUtil.sendScheduleToRegistrationListMessage(list, "msg_content:通知内容", map,
//                "notification_title:推送标题", start, end, "通知名称name");
//        if (result.getCode() == 200) {
//            System.out.println(result.getResult());
//        }
//    }

    public static void main(String[] args) {
        int i = sendToRegistrationId("120c83f760b6d7a3307",
                JPushTitleMessageEnum.getReplaceMsg(JPushTitleMessageEnum.TRANSFER_SUCCESS.getMsg(),"10USDT"),
                JPushTitleMessageEnum.TRANSFER_SUCCESS.getTitle());
        String result = i == 1? "通知发送成功" : "通知发送失败";
        System.out.println(result);
    }

    //*****************极光定时推送部分 start*****************//
    /**
     * 定时推送,推送给设备标识参数的用户（自定义消息）
     * @param registrationList 设备号列表
     * @param msg_content 通知内容
     * @param extra 扩展字段
     * @param notification_title 推送标题
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param name schedule任务的名字
     * @return 0推送失败，1推送成功
     */
    public static ResponseResult sendScheduleToRegistrationListMessage(List<String> registrationList, String msg_content, Map<String, String> extra,
                                                                       String notification_title, Date startTime, Date endTime, String name) {
        try {
            long startsSeconds = startTime.getTime()/1000;
            long endSeconds = endTime.getTime()/1000;
            long timeToLive = endSeconds - startsSeconds;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String start = format.format(startTime);
            JPushClient jPushClient = new JPushClient(masterSecret, appKey);
            PushPayload pushPayload = buildPushObject_all_aliasList_alertWithMessage(registrationList, msg_content, extra, notification_title, timeToLive);
            LOGGER.info("推送内容" + pushPayload);
            //定时任务
            ScheduleResult scheduleResult = jPushClient.createSingleSchedule(name, start, pushPayload);
            LOGGER.info("推送结果" + scheduleResult);
            if (scheduleResult.getResponseCode() == 200) {
                return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).result(scheduleResult.getSchedule_id()).build();
            }
        } catch (APIConnectionException e) {
            e.printStackTrace();
        } catch (APIRequestException e) {
            e.printStackTrace();
        }
        return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).build();
    }

    /**
     * 定时推送,推送给所有用户（自定义消息）
     * @param msg_content 通知内容
     * @param extra 扩展字段
     * @param notification_title 推送标题
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param name schedule任务的名字
     * @return 0推送失败，1推送成功
     */
    public static ResponseResult sendScheduleToAllMessage(String msg_content, Map<String, String> extra, String notification_title,
                                               Date startTime, Date endTime, String name) {
        try {
            long startsSeconds = startTime.getTime()/1000;
            long endSeconds = endTime.getTime()/1000;
            long timeToLive = endSeconds - startsSeconds;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String start = format.format(startTime);
            JPushClient jPushClient = new JPushClient(masterSecret, appKey);
            PushPayload pushPayload = buildPushObject_android_and_iosWithMessage(msg_content, extra, notification_title, timeToLive);
            LOGGER.info("推送内容" + pushPayload);
            //定时任务
            ScheduleResult scheduleResult = jPushClient.createSingleSchedule(name, start, pushPayload);
            LOGGER.info("推送结果" + scheduleResult);
            if (scheduleResult.getResponseCode() == 200) {
                return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).result(scheduleResult.getSchedule_id()).build();
            }
        } catch (APIConnectionException e) {
            e.printStackTrace();
        } catch (APIRequestException e) {
            e.printStackTrace();
        }
        return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).build();
    }

    /**
     * 修改定时推送,推送给设备标识参数的用户（自定义消息）
     *     不能更新已过期的 schedule 任务
     * @param scheduleId 定时推送id
     * @param registrationList 设备号列表
     * @param msg_content 通知内容
     * @param extra 扩展字段
     * @param notification_title 推送标题
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param name schedule任务的名字
     * @return 0修改失败，1修改成功
     */
    public static int updateScheduleToRegistrationListMessage(String scheduleId, List<String> registrationList, String msg_content, Map<String, String> extra,
                                                              String notification_title, Date startTime, Date endTime, String name) {
        int result = 0;
        try {
            JPushClient jpushClient = new JPushClient(masterSecret, appKey);
            long startsSeconds = startTime.getTime()/1000;
            long endSeconds = endTime.getTime()/1000;
            long timeToLive = endSeconds - startsSeconds;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String start = format.format(startTime);
            PushPayload pushPayload = buildPushObject_all_aliasList_alertWithMessage(registrationList, msg_content, extra, notification_title, timeToLive);
            LOGGER.info("修改后推送内容PushPayload" + pushPayload);
            //定时任务
            TriggerPayload trigger = TriggerPayload.newBuilder()
                    .setSingleTime(start).buildSingle();
            SchedulePayload payload = SchedulePayload.newBuilder()
                    .setName(name)
                    .setEnabled(true)
                    .setTrigger(trigger)
                    .setPush(pushPayload)
                    .build();
            LOGGER.info("修改后推送内容SchedulePayload" + payload);
            ScheduleResult scheduleResult = jpushClient.updateSchedule(scheduleId, payload);
            LOGGER.info("修改后推送结果" + scheduleResult);
            if (scheduleResult.getResponseCode() == 200) {
                result = 1;
            }
        } catch (APIConnectionException e) {
            LOGGER.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOGGER.error("Error response from JPush server. Should review and fix it. ", e);
            LOGGER.info("HTTP Status: " + e.getStatus());
            LOGGER.info("Error Code: " + e.getErrorCode());
            LOGGER.info("Error Message: " + e.getErrorMessage());
        }
        return result;
    }

    /**
     * 修改定时推送,推送给所有用户（自定义消息）
     *     不能更新已过期（即已经开始推送的通知）的 schedule 任务
     * @param scheduleId 定时推送id
     * @param msg_content 通知内容
     * @param extra 扩展字段
     * @param notification_title 推送标题
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param name schedule任务的名字
     * @return 0修改失败，1修改成功
     */
    public static int updateScheduleToAllMessage(String scheduleId, String msg_content, Map<String, String> extra,
                                                 String notification_title, Date startTime, Date endTime, String name) {
        int result = 0;
        try {
            JPushClient jpushClient = new JPushClient(masterSecret, appKey);
            long startsSeconds = startTime.getTime()/1000;
            long endSeconds = endTime.getTime()/1000;
            long timeToLive = endSeconds - startsSeconds;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String start = format.format(startTime);
            PushPayload pushPayload = buildPushObject_android_and_iosWithMessage(msg_content, extra, notification_title, timeToLive);
            LOGGER.info("修改后推送内容PushPayload" + pushPayload);
            //定时任务
            TriggerPayload trigger = TriggerPayload.newBuilder()
                    .setSingleTime(start).buildSingle();
            SchedulePayload payload = SchedulePayload.newBuilder()
                    .setName(name)
                    .setEnabled(true)
                    .setTrigger(trigger)
                    .setPush(pushPayload)
                    .build();
            LOGGER.info("修改后推送内容SchedulePayload" + payload);
            ScheduleResult scheduleResult = jpushClient.updateSchedule(scheduleId, payload);
            LOGGER.info("修改后推送结果" + scheduleResult);
            if (scheduleResult.getResponseCode() == 200) {
                result = 1;
            }
        } catch (APIConnectionException e) {
            LOGGER.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOGGER.error("Error response from JPush server. Should review and fix it. ", e);
            LOGGER.info("HTTP Status: " + e.getStatus());
            LOGGER.info("Error Code: " + e.getErrorCode());
            LOGGER.info("Error Message: " + e.getErrorMessage());
        }
        return result;
    }

    /**
     * 获取指定的定时任务
     *     不能获取已过期（即已经开始推送的通知）的 schedule 任务
     * @param scheduleId 定时推送id
     * @return 0查询失败，1查询成功
     */
    public static int getSchedule(String scheduleId) {
        int result = 0;
        JPushClient jpushClient = new JPushClient(masterSecret, appKey);
        try {
            ScheduleResult scheduleResult = jpushClient.getSchedule(scheduleId);
            LOGGER.info("获取后的推送结果" + scheduleResult);
            if (scheduleResult.getResponseCode() == 200) {
                result = 1;
            }
        } catch (APIConnectionException e) {
            LOGGER.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOGGER.error("Error response from JPush server. Should review and fix it. ", e);
            LOGGER.info("HTTP Status: " + e.getStatus());
            LOGGER.info("Error Code: " + e.getErrorCode());
            LOGGER.info("Error Message: " + e.getErrorMessage());
        }
        return result;
    }

    /**
     * 删除定时推送
     *     不能删除已过期（即已经开始推送的通知）的 schedule 任务
     * @param scheduleId 定时推送id
     */
    public static int DeleteSchedule(String scheduleId) {
        int result = 1;
        try {
            JPushClient jPushClient = new JPushClient(masterSecret, appKey);
            jPushClient.deleteSchedule(scheduleId);
        } catch (APIConnectionException e) {
            result = 0;
            LOGGER.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            result = 0;
            LOGGER.error("Error response from JPush server. Should review and fix it. ", e);
            LOGGER.info("HTTP Status: " + e.getStatus());
            LOGGER.info("Error Code: " + e.getErrorCode());
            LOGGER.info("Error Message: " + e.getErrorMessage());
        }
        return result;
    }
    //*****************极光定时推送部分 end*****************//

    //*****************极光通知部分 start*****************//
    /**
     * 推送给设备标识参数的用户（通知消息）
     * @param registrationId 用户极光id
     * @param msg_content 通知内容
     * @param notification_title 通知标题
     * @return 0推送失败，1推送成功
     */
    public static int sendToRegistrationId(String registrationId, String msg_content, String notification_title) {
        int result = 0;
        try {
            JPushClient jPushClient = new JPushClient(masterSecret, appKey);
            PushPayload pushPayload = buildPushObject_all_aliasList_alertWithTitle(registrationId, msg_content, notification_title);
            LOGGER.info("推送给设备标识参数的用户" + pushPayload);
            PushResult pushResult = jPushClient.sendPush(pushPayload);
            LOGGER.info("推送结果" + pushResult);
            if (pushResult.getResponseCode() == 200) {
                result = 1;
            }
        } catch (APIConnectionException e) {
            LOGGER.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            LOGGER.error("Error response from JPush server. Should review and fix it. ", e);
            LOGGER.info("HTTP Status: " + e.getStatus());
            LOGGER.info("Error Code: " + e.getErrorCode());
            LOGGER.info("Error Message: " + e.getErrorMessage());
        }
        return result;
    }
    //*****************极光通知部分 end*****************//

    /**
     * 向所有平台单个或多个指定设备号用户推送消息（自定义消息）
     * @param registrationList 设备号列表
     * @param msg_content 通知内容
     * @param extra 扩展字段
     * @param notification_title 通知标题
     * @param timeToLive 离线消息保留时长(秒)
     * @return 推送对象实体
     */
    private static PushPayload buildPushObject_all_aliasList_alertWithMessage(List<String> registrationList, String msg_content, Map<String, String> extra,
                                                                              String notification_title, Long timeToLive) {
        LOGGER.info("----------向所有平台单个或多个指定设备号用户推送自定义消息中......");
        //创建一个IosAlert对象，可指定APNs的alert、title等字段
        //IosAlert iosAlert =  IosAlert.newBuilder().setTitleAndBody("title", "alert body").build();
        return PushPayload.newBuilder()
                //指定要推送的平台，all代表当前应用配置了的所有平台，也可以传android等具体平台
                .setPlatform(Platform.all())
                //指定推送的接收对象，all代表所有人，也可以指定已经设置成功的tag或alias或该应应用客户端调用接口获取到的registration id
                .setAudience(Audience.registrationId(registrationList))
                //jpush的通知，android的由jpush直接下发，iOS的由apns服务器下发，Winphone的由mpns下发
                /*.setNotification(Notification.newBuilder()
                        //指定当前推送的android通知
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setAlert(msg_content)
                                .setTitle(notification_title)
                                //此字段为透传字段，不会显示在通知栏。用户可以通过此字段来做一些定制需求，如特定的key传要指定跳转的页面（value）
                                .addExtra(extraKey, extrasparam)
                                .build())
                        //指定当前推送的iOS通知
                        .addPlatformNotification(IosNotification.newBuilder()
                                //传一个IosAlert对象，指定apns title、title、subtitle等
                                .setAlert(msg_content)
                                //直接传alert
                                //此项是指定此推送的badge自动加1
                                .incrBadge(1)
                                //此字段的值default表示系统默认声音；传sound.caf表示此推送以项目里面打包的sound.caf声音来提醒，
                                // 如果系统没有此音频则以系统默认声音提醒；此字段如果传空字符串，iOS9及以上的系统是无声音提醒，以下的系统是默认声音
                                .setSound("default")
                                //此字段为透传字段，不会显示在通知栏。用户可以通过此字段来做一些定制需求，如特定的key传要指定跳转的页面（value）
                                .addExtra(extraKey, extrasparam)
                                //此项说明此推送是一个background推送，想了解background看：http://docs.jpush.io/client/ios_tutorials/#ios-7-background-remote-notification
                                //取消此注释，消息推送时ios将无法在锁屏情况接收
                                // .setContentAvailable(true)
                                .build())
                        .build())*/
                //Platform指定了哪些平台就会像指定平台中符合推送条件的设备进行推送。 jpush的自定义消息，
                // sdk默认不做任何处理，不会有通知提示。建议看文档http://docs.jpush.io/guideline/faq/的
                // [通知与自定义消息有什么区别？]了解通知和自定义消息的区别
                .setMessage(Message.newBuilder()
                        .setMsgContent(msg_content)
                        .setTitle(notification_title)
                        .addExtras(extra)
                        .build())
                .setOptions(Options.newBuilder()
                        //此字段的值是用来指定本推送要推送的apns环境，false表示开发，true表示生产；对android和自定义消息无意义
                        .setApnsProduction(ApnsProduction)
                        //此字段是给开发者自己给推送编号，方便推送者分辨推送记录
                        .setSendno(1)
                        //此字段的值是用来指定本推送的离线保存时长（秒），如果不传此字段则默认保存一天，最多指定保留十天；
                        .setTimeToLive(timeToLive)
                        .build())
                .build();
    }

    /**
     * 向所有平台所有用户推送消息（自定义消息）
     * @param msg_content 通知内容
     * @param extra 扩展字段
     * @param notification_title 通知标题
     * @param timeToLive 离线消息保留时长(秒)
     * @return 推送对象实体
     */
    private static PushPayload buildPushObject_android_and_iosWithMessage(String msg_content, Map<String, String> extra,
                                                                         String notification_title, Long timeToLive) {
        LOGGER.info("----------向所有平台所有用户推送自定义消息中......");
//        Message message  = Message.content(msg_body);
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.all())
                /*.setNotification(Notification.newBuilder()
                                .setAlert(msg_content)
                                .addPlatformNotification(AndroidNotification.newBuilder()
                                        .setTitle(notification_title)
                                        //此字段为透传字段，不会显示在通知栏。用户可以通过此字段来做一些定制需求，如特定的key传要指定跳转的页面（value）
                                        .addExtras(extra)
                                        .build()
                                )
                                .addPlatformNotification(IosNotification.newBuilder()
//                                //传一个IosAlert对象，指定apns title、title、subtitle等
//                                .setAlert(notification_title)
                                        //直接传alert
                                        //此项是指定此推送的badge自动加1
                                        .incrBadge(1)
                                        //此字段的值default表示系统默认声音；传sound.caf表示此推送以项目里面打包的sound.caf声音来提醒，
                                        // 如果系统没有此音频则以系统默认声音提醒；此字段如果传空字符串，iOS9及以上的系统是无声音提醒，以下的系统是默认声音
                                        .setSound("default")
                                        //此字段为透传字段，不会显示在通知栏。用户可以通过此字段来做一些定制需求，如特定的key传要指定跳转的页面（value）
                                        .addExtras(extra)
                                        //此项说明此推送是一个background推送，想了解background看：http://docs.jpush.io/client/ios_tutorials/#ios-7-background-remote-notification
                                        // .setContentAvailable(true)
                                        .build())
                                .build()
                )*/
                //Platform指定了哪些平台就会像指定平台中符合推送条件的设备进行推送。 jpush的自定义消息，
                // sdk默认不做任何处理，不会有通知提示。建议看文档http://docs.jpush.io/guideline/faq/的
                // [通知与自定义消息有什么区别？]了解通知和自定义消息的区别
                .setMessage(Message.newBuilder()
                        .setMsgContent(msg_content)
                        .setTitle(notification_title)
                        .addExtras(extra)
                        .build())
                .setOptions(Options.newBuilder()
                        //此字段的值是用来指定本推送要推送的apns环境，false表示开发，true表示生产；对android和自定义消息无意义
                        .setApnsProduction(ApnsProduction)
                        //此字段是给开发者自己给推送编号，方便推送者分辨推送记录
                        .setSendno(1)
                        //此字段的值是用来指定本推送的离线保存时长（秒），如果不传此字段则默认保存一天，最多指定保留十天，单位为秒
                        .setTimeToLive(timeToLive)
                        .build())
                .build();
    }

    /**
     * 向所有平台单个或多个指定设备号用户推送消息
     * @param registrationId 用户极光id
     * @param msg_content 通知内容
     * @param notification_title 通知标题
     * @return 通知对象实体
     */
    private static PushPayload buildPushObject_all_aliasList_alertWithTitle(String registrationId, String msg_content, String notification_title) {
        LOGGER.info("----------向所有平台单个或多个指定设备号用户推送消息中......");
        //创建一个IosAlert对象，可指定APNs的alert、title等字段
        //IosAlert iosAlert =  IosAlert.newBuilder().setTitleAndBody("title", "alert body").build();
        return PushPayload.newBuilder()
                //指定要推送的平台，all代表当前应用配置了的所有平台，也可以传android等具体平台
                .setPlatform(Platform.all())
                //指定推送的接收对象，all代表所有人，也可以指定已经设置成功的tag或alias或该应应用客户端调用接口获取到的registration id
                .setAudience(Audience.registrationId(registrationId))
                //jpush的通知，android的由jpush直接下发，iOS的由apns服务器下发，Winphone的由mpns下发
                .setNotification(Notification.newBuilder()
                        //指定当前推送的android通知
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setAlert(msg_content)
                                .setTitle(notification_title)
                                //此字段为透传字段，不会显示在通知栏。用户可以通过此字段来做一些定制需求，如特定的key传要指定跳转的页面（value）
//                                .addExtra(extraKey, extrasparam)
                                //此字段是配合安卓8.0以上用户自定义一些通知栏样式和通知声音震动等
//                                .setBuilderId()
                                .build())
                        //指定当前推送的iOS通知
                        .addPlatformNotification(IosNotification.newBuilder()
                                //传一个IosAlert对象，指定apns title、title、subtitle等
                                .setAlert(msg_content)
                                //直接传alert
                                //此项是指定此推送的badge自动加1
                                .incrBadge(1)
                                //此字段的值default表示系统默认声音；传sound.caf表示此推送以项目里面打包的sound.caf声音来提醒，
                                // 如果系统没有此音频则以系统默认声音提醒；此字段如果传空字符串，iOS9及以上的系统是无声音提醒，以下的系统是默认声音
//                                .setSound("default")
                                //此字段为透传字段，不会显示在通知栏。用户可以通过此字段来做一些定制需求，如特定的key传要指定跳转的页面（value）
//                                .addExtra(extraKey, extrasparam)
                                //此项说明此推送是一个background推送，想了解background看：http://docs.jpush.io/client/ios_tutorials/#ios-7-background-remote-notification
                                //取消此注释，消息推送时ios将无法在锁屏情况接收
                                .setContentAvailable(true)
                                //推送的时候携带 ”mutable-content":true 说明是支持iOS10的UNNotificationServiceExtension，如果不携带此字段则是普通的 Remote Notification
                                .setMutableContent(true)
                                .build())
                        .build())
                //Platform指定了哪些平台就会像指定平台中符合推送条件的设备进行推送。 jpush的自定义消息，
                // sdk默认不做任何处理，不会有通知提示。建议看文档http://docs.jpush.io/guideline/faq/的
                // [通知与自定义消息有什么区别？]了解通知和自定义消息的区别
                /*.setMessage(Message.newBuilder()
                        .setMsgContent(msg_content)
                        .setTitle(msg_title)
                        .addExtra("message extras key",extrasparam)
                        .build())*/
                .setOptions(Options.newBuilder()
                        //此字段的值是用来指定本推送要推送的apns环境，false表示开发，true表示生产；对android和自定义消息无意义
                        .setApnsProduction(ApnsProduction)
                        //此字段是给开发者自己给推送编号，方便推送者分辨推送记录
                        .setSendno(1)
                        //此字段的值是用来指定本推送的离线保存时长，如果不传此字段则默认保存一天，最多指定保留十天；
                        .setTimeToLive(86400)
                        .build())
                .build();
    }
}


