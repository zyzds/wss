package zyz.wss.async.Event;

import org.springframework.context.ApplicationEvent;

import zyz.wss.model.entity.User;

public class RegistEvent extends ApplicationEvent {
    private static final long serialVersionUID = -476004382511968095L;

    private User User;

    public RegistEvent(Object source, User user) {
        super(source);
        User = user;
    }

    public User getUser() {
        return User;
    }

    public void setUser(User user) {
        User = user;
    }
  
}
