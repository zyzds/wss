package zyz.wss.async.Listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import zyz.wss.async.Event.NotifyEvent;
import zyz.wss.repository.NotificationRepository;

@Component
public class NotifyListener implements ApplicationListener<NotifyEvent> {
    @Autowired
    private NotificationRepository notiRepo;

    @Override
    public void onApplicationEvent(NotifyEvent event) {
        notiRepo.save(event.getNfc());
    }
    
}