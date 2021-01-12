package com.xinlian.admin.biz.service.base;

import com.xinlian.admin.biz.redis.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * com.xinlian.admin.biz.service.base
 *
 * @author by Song
 * @date 2020/2/24 11:00
 */
@Service
@Slf4j
public class SequenceService {

    @Autowired
    private RedisClient redisClient;

    /**
     * 根据key生成业务单号
     * 例：
     *
     * @param prefix 自增序号前缀
     * @param length 自增序号总长度
     * @return
     */
    public String getBizOrderNo(String prefix, int length) {
        try {
            StringBuffer constractNo = new StringBuffer();
            constractNo
                    .append(prefix)
                    .append(getNo(redisClient.generateInc(constractNo.toString()), length));
            return constractNo.toString();
        } catch (Exception e) {
            log.error("生成业务编号异常", e.toString());
            return "";
        }
    }

    /**
     * 例 getNo(3L,5) = 00003
     * @param num 数字
     * @param length 共多少位
     * @return
     */
    private String getNo(Long num,int length){
        return String.format("%0"+length+"d", num);
    }
}
