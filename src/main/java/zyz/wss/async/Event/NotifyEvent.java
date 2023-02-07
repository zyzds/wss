package zyz.wss.async.Event;

import org.springframework.context.ApplicationEvent;

import zyz.wss.model.entity.Notification;

public class NotifyEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1L;

    private Notification nfc;

    public NotifyEvent(Object source, Notification nfc) {
        super(source);
        this.nfc = nfc;
    }

    public Notification getNfc() {
        return nfc;
    }

    public void setNfc(Notification nfc) {
        this.nfc = nfc;
    }
    
}