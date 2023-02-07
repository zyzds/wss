package zyz.wss.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import zyz.wss.async.Event.NotifyEvent;
import zyz.wss.model.entity.Notification;
import zyz.wss.model.entity.WSSBin;
import zyz.wss.repository.WSSBinRepository;
import zyz.wss.repository.WSSComponentRepository;

@Component
public class BinTask {
    private static final Logger log = LoggerFactory.getLogger(BinTask.class);

    @Autowired
    private WSSBinRepository binRepo;

    @Autowired
    private WSSComponentRepository compRepo;

    @Autowired
    private ApplicationEventPublisher publisher;

    /**
     * 定时任务，每天0点30清理一定时间未激活用户
     */
    @Scheduled(cron = "0 30 0 * * ?")
    public void cleanBin() {
        List<WSSBin> bins = binRepo.findAll();
        int row = 0;
        for (WSSBin bin : bins) {
            if (bin.getRemain() == 0) {
                binRepo.delete(bin);
                compRepo.delete(bin.getComponent());
                publisher.publishEvent(new NotifyEvent(this, Notification.getInstance(bin.getOwner(), "回收站文件"+bin.getComponent().getName()+"保留时间到期，已删除")));
                row++;
            }
        }
        log.info("定时任务BinTask执行完成,清除过期回收站记录{}条", row);
    }
}