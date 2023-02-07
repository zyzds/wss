package zyz.wss.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import zyz.wss.model.entity.Token;

public interface TokenRepository extends CrudRepository<Token, String> {
    @Query(nativeQuery = true, value = "SELECT * FROM token t WHERE t.user_id = :userId "+
     "ORDER BY t.login_time DESC LIMIT 1")
    Token findByUserIdLastest(@Param("userId") Integer userId);
}
