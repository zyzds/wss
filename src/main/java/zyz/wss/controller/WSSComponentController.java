package zyz.wss.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import zyz.wss.async.Event.NotifyEvent;
import zyz.wss.constant.WSSComponentConst;
import zyz.wss.model.HostHolder;
import zyz.wss.model.entity.Notification;
import zyz.wss.model.entity.User;
import zyz.wss.model.entity.WSSBin;
import zyz.wss.model.entity.WSSComponent;
import zyz.wss.model.entity.WSSFile;
import zyz.wss.repository.UserRepository;
import zyz.wss.repository.WSSBinRepository;
import zyz.wss.repository.WSSComponentRepository;
import zyz.wss.service.WSSComponentService;
import zyz.wss.service.WSSFileService;
import zyz.wss.util.WssUtil;

@Controller
@RequestMapping("/WSSComponent")
public class WSSComponentController {
    private static final Logger log = LoggerFactory.getLogger(WSSComponentController.class);

    @Autowired
    private WSSComponentService service;

    @Autowired
    private WSSFileService wssFileService;

    @Autowired
    private HostHolder holder;

    @Autowired
    private WSSComponentRepository compRepo;

    @Autowired
    private WSSBinRepository binRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RedisTemplate<String, Object> template;

    @Autowired
    private ApplicationEventPublisher publisher;

    @ResponseBody
    @RequestMapping(value = { "/folder/{id}", "/folder" })
    public JSONObject listComponent(@PathVariable(required = false) String id,
            @RequestParam(required = false) Integer limit, @RequestParam(required = false) Integer page) {
        JSONArray array = new JSONArray();
        JSONObject obj = null;
        JSONObject rtn = new JSONObject();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        WSSComponent comp = new WSSComponent();
        if (!StringUtils.isEmpty(id)) {
            WSSComponent parent = new WSSComponent();
            parent.setId(id).setOwner(holder.get());
            comp.setParent(parent);
        }
        comp.setOwner(holder.get());
        for (WSSComponent component : service.listAll(comp, false, true)) {
            obj = new JSONObject();
            obj.put("id", component.getId());
            obj.put("name", component.getName());

            obj.put("updateTime", component.getUpdateTime() == null ? "-" : sdf.format(component.getUpdateTime()));
            obj.put("type", component.getType());
            if (WSSComponentConst.USERFILE.equals(component.getType())) {
                obj.put("size", component.getSize());
                obj.put("cate", component.getCategory() == null ? "-" : component.getCategory().getName());
            } else {
                obj.put("size", "-");
                obj.put("cate", "-");
            }
            array.add(obj);
        }
        rtn.put("code", 0);
        rtn.put("msg", "success");
        rtn.put("count", array.size());
        rtn.put("data", array);
        return rtn;
    }

    @ResponseBody
    @RequestMapping("/bin")
    public JSONObject listBin(@RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer page) {
        JSONArray array = new JSONArray();
        JSONObject obj = null;
        JSONObject rtn = new JSONObject();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (WSSBin bin : binRepo.findByOwner(holder.get())) {
            if (bin.getComponent() == null) {
                continue;
            }
            obj = new JSONObject();
            obj.put("id", bin.getId());
            obj.put("name", bin.getComponent().getName());
            obj.put("type", bin.getComponent().getType());
            obj.put("deleteTime", sdf.format(bin.getDeleteTime()));
            obj.put("remain", WssUtil.daysBetween2dates(bin.getDeleteTime(), new Date()));
            array.add(obj);
        }
        rtn.put("code", 0);
        rtn.put("msg", "success");
        rtn.put("count", array.size());
        rtn.put("data", array);
        return rtn;
    }

    @ResponseBody
    @RequestMapping("/category/{id}")
    public JSONObject listAll(@PathVariable int id, @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer page) {
        JSONArray array = new JSONArray();
        JSONObject obj = null;
        JSONObject rtn = new JSONObject();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (WSSComponent component : service.listAll(id)) {
            obj = new JSONObject();
            obj.put("id", component.getId());
            obj.put("name", component.getName());

            obj.put("updateTime", component.getUpdateTime() == null ? "-" : sdf.format(component.getUpdateTime()));
            obj.put("type", component.getType());
            if (WSSComponentConst.USERFILE.equals(component.getType())) {
                obj.put("size", component.getSize());
            } else {
                obj.put("size", "-");
            }
            array.add(obj);
        }
        rtn.put("code", 0);
        rtn.put("msg", "success");
        rtn.put("count", array.size());
        rtn.put("data", array);
        return rtn;
    }

    @ResponseBody
    @RequestMapping("/search/{keyword}")
    public JSONObject search(@PathVariable String keyword, @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer page) {
        JSONArray array = new JSONArray();
        JSONObject obj = null;
        JSONObject rtn = new JSONObject();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (WSSComponent component : service.searchComponents(keyword)) {
            obj = new JSONObject();
            obj.put("id", component.getId());
            obj.put("name", component.getName());

            obj.put("updateTime", component.getUpdateTime() == null ? "-" : sdf.format(component.getUpdateTime()));
            obj.put("type", component.getType());
            if (WSSComponentConst.USERFILE.equals(component.getType())) {
                obj.put("size", component.getSize());
                obj.put("cate", component.getCategory() == null ? "-" : component.getCategory().getName());
            } else {
                obj.put("size", "-");
                obj.put("cate", "-");
            }
            array.add(obj);
        }
        rtn.put("code", 0);
        rtn.put("msg", "success");
        rtn.put("count", array.size());
        rtn.put("data", array);
        return rtn;
    }

    @RequestMapping("/download")
    public void download(HttpServletResponse resp, @RequestParam String id) throws UnsupportedEncodingException {
        File file = wssFileService.downloadFile(id);
        if (file != null && file.exists()) {
            byte[] buff = new byte[1024];
            resp.setCharacterEncoding("UTF-8");
            resp.addHeader("Content-Disposition", "attachment;fileName=" + new String(file.getName().getBytes(),"ISO8859-1"));
            resp.setContentType("application/x-download");
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                    OutputStream stream = resp.getOutputStream()) {
                int len;
                while ((len = bis.read(buff)) > 0) {
                    stream.write(buff, 0, len);
                }
            } catch (IOException e) {
                log.error("待下载文件写入输出流失败", e);
            }
        }
    }

    @ResponseBody
    @RequestMapping("/rename")
    public String rename(@RequestParam String id, @RequestParam String name, @RequestParam String type) {
        if (StringUtils.isEmpty(id) || StringUtils.isEmpty(name) || StringUtils.isEmpty(type)) {
            return "-1";
        }
        Optional<WSSComponent> opt = compRepo.findById(id);
        if (!opt.isPresent()) {
            return "0";
        }
        WSSComponent uFile = opt.get();
        if (service.checkNameRepeat(uFile.getParent() == null? null : uFile.getParent().getId(), name, type)) {
            return "-2";
        }
        uFile.setName(name);
        uFile.setUpdateTime(new Date());
        compRepo.save(uFile);
        return "1";
    }

    @ResponseBody
    @RequestMapping("/addFolder")
    public String addFolder(@RequestParam String id, @RequestParam String name) {
        if (StringUtils.isEmpty(name)) {
            return "-1";
        }
        WSSComponent comp = new WSSComponent();
        if (!StringUtils.isEmpty(id)) {
            WSSComponent parent = new WSSComponent();
            parent.setId(id);
            comp.setParent(parent);
        }
        comp.setName(
                service.checkNameRepeat(id, name, WSSComponentConst.FOLDER) ? WssUtil.nameRepeatHandle(name) : name)
                .setType(WSSComponentConst.FOLDER).setOwner(holder.get());
        compRepo.save(comp);
        return "1";
    }

    @ResponseBody
    @RequestMapping("/delete")
    @SuppressWarnings("unchecked")
    public int delete(@RequestParam String jsonArray) {
        JSONArray array = JSONArray.parseArray(jsonArray);
        Map<String, String> map = null;

        int row = 0;
        for (Object obj : array) {
            map = (Map<String, String>) obj;
            String id = map.get("id");
            WSSComponent component = new WSSComponent();
            component.setId(id);
            row += service.deleteComponent(component);
            WSSBin bin = new WSSBin(component, new Date(), WSSComponentConst.KEEPTIME, holder.get());
            binRepo.save(bin);
        }
        return row;
    }

    @ResponseBody
    @RequestMapping("/save")
    public String saveFile(@RequestParam String folderId, @RequestParam String fileName,  @RequestParam String folderPath, @RequestParam String feature) {
        if (StringUtils.isEmpty(fileName)) {
            return "-1";
        }
        wssFileService.saveFile(folderId, fileName, feature, null, folderPath, holder.get());
        return "1";
    }

    @ResponseBody
    @RequestMapping("/copy")
    public int copyFile(@RequestParam String parent, @RequestParam List<String> ids) {
        if (ids.isEmpty()) {
            return -1;
        }
        WSSComponent p = null;
        Optional<WSSComponent> opt = null;
        if (!StringUtils.isEmpty(parent)) {
            opt = compRepo.findById(parent);
            if (!opt.isPresent()) {
                return -1;
            }
            p = opt.get();
            if (!WSSComponentConst.FOLDER.equals(p.getType())) {
                return -1;
            }
        }

        int row = 0;
        for (String id : ids) {
            opt = compRepo.findById(id);
            if (opt.isPresent()) {
                row += copy(opt.get(), p);
            }
        }
        return row;
    }

    public int copy(WSSComponent src, WSSComponent parent) {
        WSSComponent target = src.clone();
        target.setId(null);
        target.setParent(parent);
        target.setName(service.checkNameRepeat(parent == null ? null : parent.getId(), src.getName(), src.getType()) ? WssUtil.nameRepeatHandle(src.getName()) : src.getName()).setOwner(holder.get()).setCategory(null);
        compRepo.save(target);

        WSSComponent example = new WSSComponent();
        example.setOwner(src.getOwner()).setParent(src);
        List<WSSComponent> childList = service.listAll(example, false, true);
        int row = 0;
        for (WSSComponent child : childList) {
            row += copy(child, target);
        }
        return ++row;
    }

    @ResponseBody
    @RequestMapping("/move")
    public int moveFile(@RequestParam String parent, @RequestParam List<String> ids) {
        if (ids.isEmpty()) {
            return -1;
        }
        WSSComponent p = null;
        Optional<WSSComponent> opt = null;
        if (!StringUtils.isEmpty(parent)) {
            opt = compRepo.findById(parent);
            if (!opt.isPresent()) {
                return -1;
            }
            p = opt.get();
            if (!WSSComponentConst.FOLDER.equals(p.getType())) {
                return -1;
            }
        }

        int row = 0;
        for (String id : ids) {
            opt = compRepo.findById(id);
            if (opt.isPresent()) {
                WSSComponent src = opt.get();
                if (isChild(p, src)) {
                    continue;
                }
                src.setParent(p);
                src.setName(service.checkNameRepeat(parent, src.getName(), src.getType()) ? WssUtil.nameRepeatHandle(src.getName()) : src.getName());
                compRepo.save(src);
                row++;
            }
        }
        return row;
    }

    @ResponseBody
    @RequestMapping("/recover")
    public int recoverFile(@RequestParam List<String> recoverList) {
        if (recoverList.isEmpty()) {
            return -1;
        }
        Optional<WSSBin> opt;
        int row = 0;
        for (String id : recoverList) {
            opt = binRepo.findById(id);
            if (opt.isPresent()) {
                WSSBin src = opt.get();
                binRepo.delete(src);
                WSSComponent component = src.getComponent();
                component.setName(service.checkNameRepeat(component.getParent() == null ? null : component.getParent().getId(), component.getName(), component.getType()) ? WssUtil.nameRepeatHandle(component.getName()) : component.getName());
                row += recoverOrDelete(component, WSSComponentConst.BIN_RECOVER);
            }
        }
        return row;
    }

    @ResponseBody
    @RequestMapping("/destory")
    public int destoryFile(@RequestParam List<String> destoryList) {
        if (destoryList.isEmpty()) {
            return -1;
        }
        Optional<WSSBin> opt;
        int row = 0;
        for (String id : destoryList) {
            opt = binRepo.findById(id);
            if (opt.isPresent()) {
                WSSBin src = opt.get();
                binRepo.delete(src);
                WSSComponent component = src.getComponent();
                row += recoverOrDelete(component, WSSComponentConst.BIN_DELETE);
            }
        }
        return row;
    }

    @ResponseBody
    @RequestMapping("/shareUrl/{id}")
    public JSONObject shareFile(@PathVariable String id) {
        JSONObject obj = new JSONObject();
        if (!StringUtils.hasText(id) || !compRepo.existsById(id)) {
            obj.put("code", -1);
            return obj;
        }
        String url = "http://localhost:8088/WSSComponent/share/" + id;
        template.opsForValue().set(id, Integer.toString(holder.get().getId()));
        template.expire(url, WSSComponentConst.SHAREURLEXPIRE, TimeUnit.DAYS);
        obj.put("shareUrl", url);
        obj.put("code", 1);
        publisher.publishEvent(new NotifyEvent(this, Notification.getInstance(holder.get(), "分享文件（夹）:" + compRepo.findById(id).get().getName() + "<br>链接：<a href"+url+">" + url+"</a>")));
        return obj;
    }

    @RequestMapping("/share/{id}")
    public String shareFileView(@PathVariable String id, Model model) {
        if (!StringUtils.hasText(id)) {
            return null;
        }
        Object tmp = template.opsForValue().get(id);
        if (tmp == null) {
            return null;
        }
        int userId = Integer.parseInt((String) tmp);
        Optional<User> optu = userRepo.findById(userId);
        Optional<WSSComponent> optc = compRepo.findById(id);
        if (!optu.isPresent() || !optc.isPresent()) {
            return null;
        }
        model.addAttribute("user", optu.get());
        model.addAttribute("file", optc.get());
        return "saveShare";
    }

    @ResponseBody
    @RequestMapping("/share/view/{id}")
    public JSONObject listShare(@PathVariable(required = false) String id,
            @RequestParam(required = false) Integer limit, @RequestParam(required = false) Integer page) {
        JSONArray array = new JSONArray();
        JSONObject obj = null;
        JSONObject rtn = new JSONObject();
        
        WSSComponent comp = new WSSComponent();
        if (StringUtils.isEmpty(id)) {
            return obj;
        }
        WSSComponent parent = new WSSComponent();
        parent.setId(id);
        comp.setParent(parent);
        for (WSSComponent component : service.listAll(comp, false, true)) {
            obj = new JSONObject();
            obj.put("id", component.getId());
            obj.put("name", component.getName());
            obj.put("type", component.getType());
            if (WSSComponentConst.USERFILE.equals(component.getType())) {
                obj.put("size", component.getSize());
            } else {
                obj.put("size", "-");
            }
            array.add(obj);
        }
        rtn.put("code", 0);
        rtn.put("msg", "success");
        rtn.put("count", array.size());
        rtn.put("data", array);
        return rtn;
    }

    @ResponseBody
    @RequestMapping("/share/saveFile")
    public int saveFile(@RequestParam List<String> saveList) {
        if (saveList.isEmpty()) {
            return -1;
        }
        Optional<WSSComponent> opt;
        int row = 0;
        for (String id : saveList) {
            opt = compRepo.findById(id);
            if (opt.isPresent()) {
                WSSComponent src = opt.get();
                row += copy(src, null);
            }
        }
        return row;
    }

    @ResponseBody
    @RequestMapping("/tool/delRepeat")
    public int delRepeat() {
        int row = 0;
        List<String> fileIds = new ArrayList<>();
        WSSComponent example = new WSSComponent();
        example.setType(WSSComponentConst.USERFILE).setOwner(holder.get());
        List<WSSComponent> cs = service.listAll(example, true, false);
        for (WSSComponent component : cs) {
            if (fileIds.contains(component.getFile().getId())) {
                component.setDiscard(true);
                WSSBin bin = new WSSBin();
                bin.setComponent(component);
                bin.setDeleteTime(new Date());
                bin.setOwner(holder.get());
                binRepo.save(bin);
                compRepo.save(component);
                row++;
            } else {
                fileIds.add(component.getFile().getId());
            }
        }
        return row;
    }

    @ResponseBody
    @RequestMapping("/tool/delEmpty")
    public int delEmpty() {

        WSSComponent example = new WSSComponent();
        example.setOwner(holder.get());
        List<WSSComponent> notEmptyIds = new ArrayList<>();
        List<WSSComponent> folderIds = new ArrayList<>();
        for (WSSComponent component : service.listAll(example, true, true)) {
            if (WSSComponentConst.FOLDER.equals(component.getType())) {
                folderIds.add(component);
            }
            if (component.getParent() == null || notEmptyIds.contains(component.getParent())) {
                continue;
            } else if (folderIds.contains(component.getParent())) {
                folderIds.remove(component.getParent());
                notEmptyIds.add(component.getParent());
            }
        }
        for (WSSComponent component : folderIds) {
            component.setDiscard(true);
            compRepo.save(component);
            WSSBin bin = new WSSBin();
            bin.setComponent(component);
            bin.setDeleteTime(new Date());
            bin.setOwner(holder.get());
            binRepo.save(bin);
        }
        return folderIds.size();
    }

    @RequestMapping("/file/show/{id}")
    public String showFile(@PathVariable String id, Model model, HttpServletResponse resp, HttpServletRequest req) {

        if (StringUtils.isEmpty(id)) {
            return null;
        }
        Optional<WSSComponent> opt = compRepo.findById(id);
        if (!opt.isPresent()) {
            return null;
        }
        WSSComponent component = opt.get();
        if (WSSComponentConst.FOLDER.equals(component.getType())) {
            return null;
        }
        WSSFile wfile = component.getFile();
        File file = new File("D:/resource/" + wfile.getLocation());
        if (!file.exists()) {
            return null;
        }
        int index = file.getName().lastIndexOf(".");
        if (index == -1) {
            return null;
        }
        String fileType = file.getName().substring(index + 1);
        String type = null;
        if (WSSComponentConst.AUDIOLIST.contains(fileType.toLowerCase())) {
            type = "audio";
        } else if (WSSComponentConst.VIDEOLIST.contains(fileType.toLowerCase())) {
            type = "video";
        } else if (WSSComponentConst.IMGLIST.contains(fileType.toLowerCase())) {
            type = "img";
        } else if ("swf".equals(fileType.toLowerCase())) {
            type = "swf";
        } else if (WSSComponentConst.TEXTLIST.contains(fileType.toLowerCase())) {
            type = "text";
            try {
                String textContent = FileUtils.readFileToString(file);
                model.addAttribute("textContent", textContent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        model.addAttribute("location", req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + wfile.getLocation());
        model.addAttribute("type", type);
        return "showFile";
    }

    private int recoverOrDelete(WSSComponent component, String type) {
        if (component == null) {
            return 0;
        }
        
        WSSComponent example = new WSSComponent();
        example.setOwner(holder.get()).setParent(component).setDiscard(true);
        List<WSSComponent> childList = service.listAll(example, false, true);
        int row = 0;
        for (WSSComponent child : childList) {
            row += recoverOrDelete(child, type);
        }
        switch (type) {
            case WSSComponentConst.BIN_DELETE:
                compRepo.delete(component);
            break;
            case WSSComponentConst.BIN_RECOVER: 
                component.setDiscard(false);
                compRepo.save(component);
            break;
            default:break;
        }
        return ++row;
    }

    public boolean isChild(WSSComponent parent, WSSComponent child) {
        if (child.getParent() == null) {
            return parent == null;
        }
        return parent == null ? false : (child.getParent().getId() == parent.getId());
    }

}