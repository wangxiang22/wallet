package com.xinlian.member.biz.service.impl;

import com.xinlian.member.biz.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

@Service
public class MailServiceImpl implements MailService {
    private static Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);

    //@Value("${spring.mail.username}")
    private String from;

    //private String[][] mailUser = {{"RCKET_APP_3@163.com","RocketGo20202933"},
    //        {"rcket_app_2@163.com","RocketGo20202933"},{"rcket_app_5@163.com","RocketGo20202933"}};
    private String[][] mailUser = {{"cat_app@163.com","CatGo202021110"},
            {"cat_auth@163.com","CaTGo202021109"}};


    /*@Autowired
    private JavaMailSenderImpl javaMailSender;*/

    /**
     * 发送文本邮件
     * @param to  发给谁
     * @param subject 主题
     * @param content 内容
     */
   /* @Override
    public void sendSimpleMail(String to, String subject, String content){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(content);
        simpleMailMessage.setFrom(from);
        javaMailSender.send(simpleMailMessage);
    }*/

    @Override
    public void sendSimpleMailCode(String to, String subject, String content){
//        String[] strs = mailUser[new Random().nextInt(mailUser.length)];
//        EmailCodeUtils.sendEmailCode("smtp.163.com", strs[0],  strs[0],
//                strs[1],  to,  subject,  content);
    }

    /**
     * 发送html邮件
     * @param to  发给谁
     * @param subject 主题
     * @param content 内容
     */
   /* @Override
    public void sendHtmlMail(String to, String subject, String content) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content,true);
        helper.setFrom(from);
        javaMailSender.send(mimeMessage);
    }*/

    /**
     * 发送附件邮件
     * @param to
     * @param subject
     * @param content
     * @param filePath
     * @throws MessagingException
     */
  /*  @Override
    public void sendAttachMENTMail(String to,String subject,String content,String filePath) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper=new MimeMessageHelper(mimeMessage,true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content,true);
        helper.setFrom(from);
        FileSystemResource fileSystemResource = new FileSystemResource(new File(filePath));
        String filename = fileSystemResource.getFilename();
        helper.addAttachment(filename, fileSystemResource);
        //发送两个附件
        helper.addAttachment(filename+"_second", fileSystemResource);
        javaMailSender.send(mimeMessage);
    }*/

    /**
     * 发送图片邮件
     * @param to
     * @param subject
     * @param content
     * @param filePath
     * @param id
     * @throws MessagingException
     */
   /* @Override
    public void sendInLineResouceMail(String to,String subject,String content,String filePath,String id)  {
        logger.info("发送静态邮件开始，{}，{}，{}，{}，{}",to,subject,content,filePath,id);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper= null;
        try {
            helper = new MimeMessageHelper(mimeMessage,true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content,true);
            helper.setFrom(from);
            FileSystemResource fileSystemResource = new FileSystemResource(new File(filePath));
            helper.addInline(id,fileSystemResource);
            javaMailSender.send(mimeMessage);
            logger.error("发送邮件成功");
        } catch (MessagingException e) {
            logger.error("发送邮件失败",e);
        }
    }*/

}
