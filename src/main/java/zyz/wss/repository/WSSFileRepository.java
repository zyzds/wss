package zyz.wss.repository;

import org.springframework.data.repository.CrudRepository;
import zyz.wss.model.entity.WSSFile;

public interface WSSFileRepository extends CrudRepository<WSSFile, String> {
    /**
     * 根据特征码检查文件是否存在
     * @param feature
     * @return
     */
    boolean existsByFeature(String feature);

    WSSFile findByFeature(String feature);
}
