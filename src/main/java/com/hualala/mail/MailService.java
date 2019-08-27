package com.hualala.mail;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author YuanChong
 * @create 2019-08-27 10:41
 * @desc
 */
@Log4j2
@Service
public class MailService {


    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    private ExecutorService mailThreadPool = Executors.newSingleThreadExecutor();


    public void sendMail(String to, String subject, String content) {
        mailThreadPool.execute(() -> doSend(to, subject, content));
    }

    private void doSend(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        log.info("准备发送邮件 from:{} to:{} subject:{} content:{}", from, to, subject, content);
        mailSender.send(message);
    }

}
