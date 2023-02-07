package zyz.wss.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import zyz.wss.model.entity.User;
import zyz.wss.model.entity.WSSComponent;

public interface WSSComponentRepository extends JpaRepository<WSSComponent, String> {
    List<WSSComponent> findByParentAndOwner(WSSComponent parent, User owner);

    List<WSSComponent> findByParentAndNameAndOwner(WSSComponent parent, String name, User user);

    WSSComponent findByIdAndType(String id, String type);
}
