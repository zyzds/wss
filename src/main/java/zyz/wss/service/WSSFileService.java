package zyz.wss.service;

import java.io.File;

import zyz.wss.model.MultipartDTO;
import zyz.wss.model.entity.User;

public interface WSSFileService {
    /**
     * 检查文件是否已存在
     * @param feature
     * @return
     */
    boolean checkFileExistence(String feature);
    
    /**
     * 上传文件
     * @param mDto
     */
    void sliceUpload(MultipartDTO mDto);

    /**
     * 文件下载
     * @param userFileId  用户文件
     * @return  目标
     */
    File downloadFile(String userFileId);

    /**
     * 文件合并
     * @param chunks
     * @param feature
     * @param fileName
     * @return
     */
    boolean mergeFile(int chunks, String feature, String fileName, String folderId, String folderPath);
    
    /**
     * 文件保存
     * @param folderId
     * @param fileName
     * @param feature
     * @param size
     * @param folderPath
     * @param user
     */
    void saveFile(String folderId, String fileName, String feature, Long size, String folderPath, User user);
}