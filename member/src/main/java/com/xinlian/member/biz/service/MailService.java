package com.xinlian.member.biz.service;

public interface MailService {

    //void sendSimpleMail(String to, String subject, String content);

    void sendSimpleMailCode(String to, String subject, String content);

    /*void sendHtmlMail(String to, String subject, String content) throws MessagingException;

    void sendAttachMENTMail(String to,String subject,String content,String filePath) throws MessagingException;

    void sendInLineResouceMail(String to,String subject,String content,String filePath,String id);*/
}
