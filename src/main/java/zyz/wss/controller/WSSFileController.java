package zyz.wss.controller;

import com.alibaba.fastjson.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import zyz.wss.model.HostHolder;
import zyz.wss.model.MultipartDTO;
import zyz.wss.service.WSSFileService;

@Controller
@RequestMapping("/file")
public class WSSFileController {
    @Autowired
    private WSSFileService fileService;

    @Autowired
    private HostHolder holder;

    @PostMapping("/checkRepeat")
    @ResponseBody
    public boolean checkRepeat(@RequestParam String feature) {
        return fileService.checkFileExistence(feature);
    }

    @ResponseBody
    @RequestMapping("/uploadDirectory")
    public JSONObject uploadDerectory(MultipartDTO mDto) {
        if (holder.get() == null) {
            return new JSONObject();
        }
        fileService.sliceUpload(mDto);
        JSONObject obj = new JSONObject();
        obj.put("id", mDto.getId());
        obj.put("chunk", mDto.getChunk());
        obj.put("chunks", mDto.getChunks());
        return obj;
    }

    @ResponseBody
    @RequestMapping("/merge")
    public boolean mergeFile(@RequestParam String feature, @RequestParam String fileName, @RequestParam int chunks, @RequestParam String folderId, @RequestParam String folderPath) {
        if (holder.get() == null) {
            return false;
        }

        return fileService.mergeFile(chunks, feature, fileName, folderId, folderPath);
    }
}