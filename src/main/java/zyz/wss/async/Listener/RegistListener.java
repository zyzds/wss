package zyz.wss.async.Listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import zyz.wss.service.MailService;
import zyz.wss.async.Event.RegistEvent;

@Component
public class RegistListener implements ApplicationListener<RegistEvent> {
    @Autowired
    private MailService mailService;

    private static final String SUBJECT = "验证邮箱";

    @Override
    public void onApplicationEvent(RegistEvent event) {
        mailService.sendTemplateMail(event.getUser().getEmail(), SUBJECT, String.valueOf(event.getUser().getId()));
    }

}