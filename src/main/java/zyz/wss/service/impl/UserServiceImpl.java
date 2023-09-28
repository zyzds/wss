package zyz.wss.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import zyz.wss.constant.LoginConst;
import zyz.wss.model.entity.Notification;
import zyz.wss.model.entity.Token;
import zyz.wss.model.entity.User;
import zyz.wss.repository.TokenRepository;
import zyz.wss.repository.UserRepository;
import zyz.wss.service.UserService;
import zyz.wss.async.Event.NotifyEvent;
import zyz.wss.async.Event.RegistEvent;
import zyz.wss.util.SensitiveWord;
import zyz.wss.util.VerificationCodeUtil;
import zyz.wss.util.VerificationCodeUtil.VerificationCode;
import zyz.wss.util.WssUtil;

@Service
public class UserServiceImpl implements UserService, ApplicationEventPublisherAware, InitializingBean {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private RedisTemplate<String, Object> template;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    @Override
    public Map<String, Object> login(String nameOrEmail, String password, String IP) {
        Map<String, Object> msg = new HashMap<>();
        if (StringUtils.isEmpty(nameOrEmail) || StringUtils.isEmpty(password)) {
            msg.put("result", LoginConst.WRONG);
            msg.put("reason", "登录信息为空");
            return msg;
        }
        User nameUser = userRepository.findByName(nameOrEmail);
        User emailUser = userRepository.findByEmail(nameOrEmail);
        if ((nameUser == null || nameUser.getActive() == false)
                && (emailUser == null || emailUser.getActive() == true)) {
            msg.put("result", LoginConst.WRONG);
            msg.put("reason", "登录信息错误");
            return msg;
        }
        User user = nameUser != null ? nameUser : emailUser;
        if (!user.getPassword().equals(WssUtil.MD5Encode(password))) {
            msg.put("result", LoginConst.WRONG);
            msg.put("reason", "登录信息错误");
            return msg;
        }
        // 当登录时发现数据库存有该账号未失效的登录记录，删除
        Token token = tokenRepository.findByUserIdLastest(user.getId());
        if (token != null && !token.isExpire()) {
            tokenRepository.delete(token);
        }

        Token newToken = new Token(user, new Date(System.currentTimeMillis()), IP);
        tokenRepository.save(newToken);
        msg.put("token", newToken);

        msg.put("result", LoginConst.SUCCESS);
        msg.put("reason", "登录成功");
        return msg;
    }

    @Override
    public Map<String, Object> regist(String name, String email, String password) {
        Map<String, Object> msg = new HashMap<>();
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(email) || StringUtils.isEmpty(password)) {
            msg.put("result", LoginConst.WRONG);
            msg.put("reason", "注册信息为空");
            return msg;
        }
        // 格式验证
        if (!email.matches("[\\w\\.\\-]+@+\\w+\\.[\\w\\.]+") || name.length() > 10 || password.length() < 5) {
            msg.put("result", LoginConst.WRONG);
            msg.put("reason", "注册信息格式不正确");
            return msg;
        }
        // 敏感信息过滤
        name = HtmlUtils.htmlEscape(name);
        if (SensitiveWord.hasSensitiveWord(name)) {
            msg.put("result", LoginConst.WRONG);
            msg.put("reason", "注册信息包含敏感信息");
            return msg;
        }
        // 重名校验
        if (userRepository.findByName(name) != null || userRepository.findByEmail(email) != null) {
            msg.put("result", LoginConst.WRONG);
            msg.put("reason", "注册信息已存在");
            return msg;
        }
        // 信息验证成功...
        password = WssUtil.MD5Encode(password);
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(password);
        user.setRegistTime(new Date(System.currentTimeMillis()));
        userRepository.save(user);
        // 发送邮件
        publisher.publishEvent(new RegistEvent(this, user));
        msg.put("result", LoginConst.SUCCESS);
        return msg;
    }

    @Override
    public boolean checkVerificationCode(String id, String answer) {
        if (StringUtils.isEmpty(id) || StringUtils.isEmpty(answer)) {
            return false;
        }
        String rightAnswer = (String) template.opsForValue().get(id);
        return answer.equals(rightAnswer);
    }

    @Override
    public boolean activeUser(int id) {
        Optional<User> optional = userRepository.findById(id);
        if (!optional.isPresent()) {
            return false;
        }
        User u = optional.get();
        u.setActive(true);
        publisher.publishEvent(new NotifyEvent(this, Notification.getInstance(u, "欢迎用户注册！")));
        return true;
    }

    @Override
    public VerificationCode generateVCode(String id) {
        VerificationCode code = VerificationCodeUtil.VerificationCodeGenerator();
        template.opsForValue().set(id, code.getAnswer());
        template.expire(id, VerificationCode.EXPIRE, TimeUnit.SECONDS);
        return code;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.userRepository.findByName("admin") == null) {
            User admin = new User();
            admin.setName("admin");
            admin.setPassword(WssUtil.MD5Encode("0"));
            admin.setRegistTime(new Date());
            admin.setType(User.UserType.ADMIN);
            this.userRepository.save(admin);
        }
    }
}