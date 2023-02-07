package zyz.wss.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import zyz.wss.async.Event.NotifyEvent;
import zyz.wss.constant.WSSComponentConst;
import zyz.wss.model.HostHolder;
import zyz.wss.model.MultipartDTO;
import zyz.wss.model.entity.Notification;
import zyz.wss.model.entity.User;
import zyz.wss.model.entity.WSSComponent;
import zyz.wss.model.entity.WSSFile;
import zyz.wss.model.entity.WSSFolder;
import zyz.wss.repository.WSSComponentRepository;
import zyz.wss.repository.WSSFileRepository;
import zyz.wss.service.WSSFileService;
import zyz.wss.util.WssUtil;

@Service
public class WSSFileServiceImpl implements WSSFileService {
    private static final Logger log = LoggerFactory.getLogger(WSSFileServiceImpl.class);

    @Autowired
    private WSSFileRepository wssFileRepository;

    @Autowired
    private WSSComponentRepository compRepo;

    @Value("${spring.servlet.multipart.location}")
    private String baseFilePath;

    @Autowired
    private HostHolder holder;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Override
    public boolean checkFileExistence(String feature) {
        Assert.hasText(feature, "MD5 cannot be none");
        return wssFileRepository.existsByFeature(feature);
    }

    @Override
    public void sliceUpload(MultipartDTO mDto) {
        log.info("==============slice upload begin===============");
        log.info("filename:{},chunk:{},totalChunk:{}", mDto.getName(), mDto.getChunk(), mDto.getChunks());

        String fileName = mDto.getFile().getOriginalFilename();
        int index = fileName.lastIndexOf(".");
        String typeName = index == -1 ? WSSComponentConst.DEFAULT : (fileName.substring(fileName.lastIndexOf(".") + 1));
        String tmpDirPath = baseFilePath + typeName + "/" + mDto.getFeature();
        String tmpFileName = (index == -1 ? fileName : fileName.substring(0, index)) + "_" + mDto.getChunk() + ".tmp";
        File tmpDir = new File(tmpDirPath);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        
        File tmpFile = new File(tmpDir, tmpFileName);
        try (FileInputStream in = (FileInputStream) mDto.getFile().getInputStream();
            FileOutputStream out = new FileOutputStream(tmpFile)) {
            byte[] buf = new byte[1024];        
            int bytesRead;        
            while ((bytesRead = in.read(buf)) > 0) {
                out.write(buf, 0, bytesRead);
            }
        } catch(IOException e) {
            log.error("临时文件创建失败!filename:{},chunk:{}", mDto.getName(), mDto.getChunk());
        }

    }

    /**
     * 重命名并移动到新目录
     * 
     * @param file
     * @param newFileName
     * @param filePath
     */
    private void renameFile(File file, String newFileName, String filePath) {
        if (file.exists() && !StringUtils.isEmpty(newFileName)) {
            file.renameTo(new File(filePath, newFileName));
        }
    }

    public boolean mergeFile(int chunks, String feature, String fileName, String folderId, String folderPath) {

        int index = fileName.lastIndexOf(".");
        String typeName = index == -1 ? WSSComponentConst.DEFAULT : (fileName.substring(fileName.lastIndexOf(".") + 1));
        String filePath = baseFilePath + typeName + "/";
        String tmpDirPath = baseFilePath + typeName + "/" + feature;

        File tmpDir = new File(tmpDirPath);
        if (!tmpDir.exists() || tmpDir.listFiles().length != chunks) {
            return false;
        }

        //文件合并
        File outputFile = new File(filePath, fileName);
        try (FileChannel outputFileChannel = new FileOutputStream(outputFile, true).getChannel()) {
            File[] tmpFiles = tmpDir.listFiles();
            Map<Integer, File> fileMap = chunk2FileMap(tmpFiles);

            for (int i = 0; i < tmpFiles.length; i++) {
                File tmpFile = fileMap.get(i);
                try (FileChannel blk = new FileInputStream(tmpFile).getChannel()) {
                    outputFileChannel.transferFrom(blk, outputFileChannel.size(), blk.size());
                } catch (IOException e) {
                    log.error("文件合并失败，文件名：{}", tmpFile.getName());
                }
            }
            
            //临时文件删除
            for (int i = 0; i < tmpFiles.length; i++) {
                tmpFiles[i].delete();
            }

            log.info("===============file merge complete filename:{}==========", fileName);
        } catch (IOException e) {
            log.error("设置上传进度文件IO错误", e);
        }

        //目标文件转移出临时目录
        renameFile(outputFile, fileName, filePath);

        //临时文件夹删除
        tmpDir.delete();

        //文件保存
        saveFile(folderId, fileName, feature, outputFile.length(), folderPath, holder.get());

        return true;
    }
    
    /**
     * 创建chunk-file map
     */
    private static Map<Integer, File> chunk2FileMap(File[] files) {
        Map<Integer, File> fileMap = new HashMap<>(files.length);
        for (File tmpFile : files) {
            String tmp = tmpFile.getName().substring(tmpFile.getName().lastIndexOf("_") + 1, tmpFile.getName().lastIndexOf("."));
            int chunk = Integer.parseInt(tmp);
            fileMap.put(chunk, tmpFile);
        }
        return fileMap;
    }

    /**
     * 解析文件父目录
     * 
     * @param originalName 文件上传名
     * @return 文件父目录
     */
    private List<String> parseDirectory(String originalName) {
        List<String> directories = new ArrayList<>();
        if (StringUtils.isEmpty(originalName)) {
            return directories;
        }
        String[] strs = originalName.split("/");
        if (strs.length <= 1) {
            return new ArrayList<String>();
        }
        strs = Arrays.copyOfRange(strs, 0, strs.length - 1);
        return Arrays.asList(strs);
    }

    /**
     * 生成目录链
     * 
     * @param head
     * @param nodes
     * @param user
     * @return
     */
    private WSSComponent generateFolderChain(WSSComponent head, List<String> nodes, User user) {
        if (nodes == null || nodes.size() == 0) {
            return head;
        }

        boolean isExist = false;
        for (String node : nodes) {
            WSSComponent folder = null;
            if (!isExist) {
                List<WSSComponent> fs = compRepo.findByParentAndNameAndOwner(head, node, user);
                if (fs.size() == 1) {
                    folder = fs.get(0);
                    head = folder;
                    continue;
                } else {
                    folder = new WSSComponent();
                    isExist = true;
                }
            } else {
                folder = new WSSFolder();
            }
            folder.setName(node).setOwner(user).setParent(head).setType(WSSComponentConst.FOLDER);
            compRepo.save(folder);
            head = folder;
        }
        return head;
    }

    @Override
    public void saveFile(String folderId, String fileName, String feature, Long size, String folderPath, User user) {
        if (user == null) {
            return;
        }

        WSSComponent folder = null;
        if (!StringUtils.isEmpty(folderId)) {
            Optional<WSSComponent> opt = compRepo.findById(folderId);
            if (!opt.isPresent()) {
                return;
            }
            folder = opt.get();
        }
        
        int index = fileName.lastIndexOf(".");
        String typeName = index == -1 ? WSSComponentConst.DEFAULT : (fileName.substring(fileName.lastIndexOf(".") + 1));
        //String filePath = baseFilePath + typeName + "/";
        // 新建文件
        WSSFile wssFile = wssFileRepository.findByFeature(feature);
        if (wssFile == null) {
            wssFile = new WSSFile();
            wssFile.setFeature(feature);
            wssFile.setLocation("/upload/" + typeName + "/" + fileName);
            wssFile.setSize(size);
            wssFileRepository.save(wssFile);
        }

        WSSComponent parent = generateFolderChain(folder, parseDirectory(folderPath), user);

        // 新建WSSUserFile
        WSSComponent userFile = new WSSComponent();
        int dupSize = compRepo.findByParentAndNameAndOwner(parent, fileName, user).size();
        if (dupSize > 0) {
            fileName = WssUtil.nameRepeatHandle(fileName);
        }
        userFile.setFile(wssFile);
        userFile.setName(fileName);
        userFile.setOwner(user);
        userFile.setParent(parent);
        userFile.setType(WSSComponentConst.USERFILE);
        compRepo.save(userFile);
        publisher.publishEvent(new NotifyEvent(this, Notification.getInstance(holder.get(), "上传文件" + fileName)));
    }

    @Override
    public File downloadFile(String userFileId) {
        Optional<WSSComponent> opt = compRepo.findById(userFileId);
        if (!opt.isPresent()) {
            return null;
        }
        WSSComponent userFile = opt.get();
        WSSFile wssFile = userFile.getFile();
        if (wssFile == null) {
            return null;
        }
        return new File("D:/resource/" + wssFile.getLocation());
    }
    
}