package com.xinlian.rabbitMq;

/**
 * <p>
 *  公共处理接口
 * </p>
 *
 * @author cms
 * @since 2020-04-14
 */
public interface BaseHandler {

    void handler(BaseMessage baseMessage);

}
