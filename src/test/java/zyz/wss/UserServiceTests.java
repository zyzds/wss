package zyz.wss;

import java.time.LocalDateTime;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import zyz.wss.constant.LoginConst;
import zyz.wss.model.entity.User;
import zyz.wss.model.entity.User.UserType;
import zyz.wss.repository.UserRepository;
import zyz.wss.service.UserService;
import zyz.wss.util.WssUtil;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class UserServiceTests {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void registTest() {
        Map<String, Object> msg = userService.regist("zyz", "1521612231@qq.com", "123456");
        System.out.println(msg.toString());
    }

    @Test
    @Rollback(value = false)
    public void userTest() {
        User u = new User();
        u.setName("john");
        u.setEmail("zyz@qq.com");
        u.setType(UserType.VIP);
        u.setPassword("123");
        u.setRegistTime(WssUtil.localDateTimeToDateConverter(LocalDateTime.now().minusDays(LoginConst.ACTIVE_EXPIRE_TIME)));
        /* userRepository.save(u);
        User a = userRepository.findById(u.getId()).get(); */
        System.out.println(u.toString());
        
        //userService.activeUser(15);
        org.springframework.util.Assert.notEmpty(userRepository.findByActiveTrue(), "list is empty"); 
        org.springframework.util.Assert.state(userRepository.deleteByActiveFalseAndRegistTimeBefore(WssUtil.localDateTimeToDateConverter(LocalDateTime.now().minusDays(LoginConst.ACTIVE_EXPIRE_TIME))) > 0, "record length is zero");
    }

    @Test
    @Rollback(value = false)
    public void activeUser() {
        //激活2号用户
        userService.activeUser(2);
    }

    @Test
    public void VCodeTest() {
        /* VerificationCode code = userService.generateVCode();
        org.springframework.util.Assert.isTrue(userService.checkVerificationCode(code.getId(), code.getAnswer()), "redis is not worked"); */
    }
}