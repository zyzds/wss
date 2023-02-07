package zyz.wss.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import zyz.wss.model.entity.User;
import zyz.wss.model.entity.WSSCategory;

public interface WSSCategoryRepository extends CrudRepository<WSSCategory, Integer> {
    List<WSSCategory> findByUser(User user);

    boolean existsByUserAndName(User user, String name);
}
