package zyz.wss;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTests {
    @Autowired
    private RedisTemplate<String, Object> template;

    @Test
    public void redisConectionTest() {
        Assert.notNull(template.getConnectionFactory(), "redis连接失败");
    }

    @Test
    public void redisCommandTest() {
        template.opsForValue().set("name", "zyz");
        Assert.hasText((String)template.opsForValue().get("name"), "该key不存在");
    }
}