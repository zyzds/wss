package zyz.wss.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import zyz.wss.model.entity.Notification;
import zyz.wss.model.entity.User;

public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findByReceiverOrderByTimeDesc(User receiver);
}
