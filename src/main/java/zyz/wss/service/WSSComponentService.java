package zyz.wss.service;

import java.io.OutputStream;
import java.util.List;

import zyz.wss.model.entity.WSSComponent;

public interface WSSComponentService {
    boolean checkNameRepeat(String parentId, String name, String type);

    /**
     * 新建文件（夹）
     * @param parentId
     * @param name
     * @param type
     */
    void createComponent(String parentId, String name, String type);

    /**
     * 删除文件（夹）
     * @param component
     * @return
     */
    int deleteComponent(WSSComponent component);

    /**
     * 批量删除
     * @param cList
     * @return
     */
    int deleteAll(List<WSSComponent> cList);

    /**
     * 更新文件（夹）
     * @param component
     */
    void updateComponent(WSSComponent component);

    /**
     * 查找所有文件（夹）
     * @return
     */
    List<WSSComponent> listAll(WSSComponent component, boolean parentIgnore, boolean typeIgnore);

    /**
     * 查找所有文件（夹）并排序
     * @param component
     * @param orderBy   排序方式
     * @param orderByFiled  排序字段
     * @return
     */
    List<WSSComponent> listAll(WSSComponent component, String orderBy, String orderByFiled, boolean parentIgnore, boolean typeIgnore);

    /**
     * 查找分类下所有文件
     * @param categoryId
     * @return
     */
    List<WSSComponent> listAll(Integer categoryId);

    WSSComponent listOne(String id, String type);

    /**
     * 根据关键词查找文件（夹）
     * @param keyword
     * @return
     */
    List<WSSComponent> searchComponents(String keyword);

    /**
     * 文件下载
     * @param ops
     * @param id
     */
    void download(OutputStream ops, String id);
}