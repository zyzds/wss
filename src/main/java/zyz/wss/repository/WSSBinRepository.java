package zyz.wss.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import zyz.wss.model.entity.User;
import zyz.wss.model.entity.WSSBin;

public interface WSSBinRepository extends JpaRepository<WSSBin, String> {
    List<WSSBin> findByOwner(User owner);
}