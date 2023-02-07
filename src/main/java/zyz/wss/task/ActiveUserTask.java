package zyz.wss.task;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import zyz.wss.constant.LoginConst;
import zyz.wss.repository.UserRepository;
import zyz.wss.util.WssUtil;

@Component
public class ActiveUserTask {
    private static final Logger log = LoggerFactory.getLogger(ActiveUserTask.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * 定时任务，每天0点清理一定时间未激活用户
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanNotActiveUser() {
        LocalDateTime time = LocalDateTime.now().minusDays(LoginConst.ACTIVE_EXPIRE_TIME);
        int result = userRepository.deleteByActiveFalseAndRegistTimeBefore(WssUtil.localDateTimeToDateConverter(time));
        log.info("定时任务ActiveUserTask执行完成,清除未激活用户记录{}条", result);
    }
}