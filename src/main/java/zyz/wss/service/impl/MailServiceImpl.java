package zyz.wss.service.impl;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import zyz.wss.service.MailService;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private TemplateEngine engine;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${mail.fromMail.addr}")
    private String from;

    @Override
    public void sendTemplateMail(String to, String subject, String id) {
        Context context = new Context();
        //待激活用户ID
        context.setVariable("id", id);
        String emailContent = engine.process("emailTemplate", context);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            //收件方
            helper.setTo(to);
            //发件方
            helper.setFrom(from);
            //邮件主题
            helper.setSubject(subject);
            //邮件内容
            helper.setText(emailContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}