package zyz.wss.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import zyz.wss.model.entity.User;

public interface UserRepository extends CrudRepository<User, Integer> {
    User findByName(String name);
    
    User findByEmail(String email);

    /**
     * 查询所有激活用户
     */
    List<User> findByActiveTrue();

    /**
     * 删除未在指定时间范围激活的用户
     * @param date  当前时间-时间段
     * @return  删除记录数
     */
    Integer deleteByActiveFalseAndRegistTimeBefore(Date date);
}
