package com.xinlian.member.biz.service;

/**
 * com.xinlian.member.biz.service
 *
 * @date 2020/2/9 19:45
 */
public interface AliyunEmailService {

    boolean sendEmailCode(String toEmailAddress,String emailCode,Integer useType);

    void checkOftenFlag(String emailAddress,Integer useType);
}
