package zyz.wss.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import zyz.wss.service.WSSFileService;

@Controller
public class HelloController {
    private static final Logger log = LoggerFactory.getLogger(HelloController.class);

    @Autowired
    private WSSFileService wssFileService;

    @ResponseBody
    @RequestMapping("/hello")
    public String Hello() {
        return "Hello World!";
    }

    @ResponseBody
    @RequestMapping("/uploadDerectory")
    public String uploadDerectory(HttpServletRequest request) {
        MultipartHttpServletRequest req = (MultipartHttpServletRequest) request;
        List<MultipartFile> files = req.getFiles("file");
        System.out.println(files.size());
        for (MultipartFile file : files) {
            System.out.println(file.getOriginalFilename() + "-" + file.getName());
        }
        return "1";
    }

    @RequestMapping("/downloadFile")
    public String name(@RequestParam String userFileId, HttpServletResponse resp) {
        File file = wssFileService.downloadFile(userFileId);
        if (file != null && file.exists()) {
            byte[] buff = new byte[1024];
            resp.addHeader("Content-Disposition", "attachment;fileName=" + file.getName());
            resp.setContentType("application/x-download");
            try (
                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                    OutputStream stream = resp.getOutputStream()
                ) {
                int read = bis.read(buff);
                while (read != -1) {
                    stream.write(buff, 0, buff.length);
                    stream.flush();
                    read = bis.read(buff);
                }
            } catch (IOException e) {
                log.error("待下载文件写入输出流失败", e);
            }
        }
        return null;
    }
}
