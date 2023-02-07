package zyz.wss.controller;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import zyz.wss.constant.WSSComponentConst;
import zyz.wss.model.HostHolder;
import zyz.wss.model.entity.Notification;
import zyz.wss.model.entity.WSSCategory;
import zyz.wss.model.entity.WSSComponent;
import zyz.wss.repository.NotificationRepository;
import zyz.wss.repository.WSSCategoryRepository;
import zyz.wss.repository.WSSComponentRepository;
import zyz.wss.service.WSSComponentService;


@Controller
public class IndexController {
    @Autowired
    private WSSCategoryRepository cateRepo;

    @Autowired
    private WSSComponentRepository compRepo;

    @Autowired
    private WSSComponentService service;

    @Autowired
    private NotificationRepository notiRepo;

    @Autowired
    private HostHolder holder;

    @RequestMapping(value={"/index", "/"}, method=RequestMethod.GET)
    public String requestMethodName(Model model) {
        List<Notification> ns = notiRepo.findByReceiverOrderByTimeDesc(holder.get());
        int unread = 0;
        for (Notification n : ns) {
            if (!n.getStatus()) {
                unread++;
                n.setStatus(true);
                notiRepo.save(n);
            }
        }
        model.addAttribute("ns", ns);
        model.addAttribute("unread", unread);
        model.addAttribute("cateList", cateRepo.findByUser(holder.get()));
        return "index";
    }

    @ResponseBody
    @RequestMapping("/category/add")
    public int addCategory(String name) {
        if (!StringUtils.hasText(name) || cateRepo.existsByUserAndName(holder.get(), name)) {
            return -1;
        }
        WSSCategory cate = new WSSCategory();
        cate.setName(name);
        cate.setUser(holder.get());
        cateRepo.save(cate);
        return 1;
    }

    @ResponseBody
    @RequestMapping("/category/all")
    public List<WSSCategory> listAll() {
        return cateRepo.findByUser(holder.get());
    }

    @ResponseBody
    @RequestMapping("/category/addToCate")
    public JSONObject addToCate(@RequestParam Integer cateId, @RequestParam List<String> ids) {
        JSONObject obj = new JSONObject();
        if (!cateRepo.existsById(cateId) || ids.isEmpty()) {
            obj.put("size", -1);
            return obj;
        }
        WSSCategory cate = cateRepo.findById(cateId).get();
        WSSComponent component;
        int row = 0;
        for (String id : ids) {
            component = compRepo.findByIdAndType(id, WSSComponentConst.USERFILE);
            if (component != null) {
                component.setCategory(cate);
                compRepo.save(component);
            }
            row++;
        }
        obj.put("size", row);
        obj.put("name", cate.getName());
        return obj;
    }

    @ResponseBody
    @RequestMapping("/category/delFromCate")
    public JSONObject delFromCate(@RequestParam List<String> ids) {
        JSONObject obj = new JSONObject();
        if (ids.isEmpty()) {
            obj.put("size", -1);
            return obj;
        }
        WSSComponent component;
        int row = 0;
        for (String id : ids) {
            component = compRepo.findByIdAndType(id, WSSComponentConst.USERFILE);
            if (component != null) {
                component.setCategory(null);
                compRepo.save(component);
            }
            row++;
        }
        obj.put("size", row);
        return obj;
    }

    @ResponseBody
    @RequestMapping("/category/del")
    public int delCate(@RequestParam Integer cateId) {
        if (!cateRepo.existsById(cateId)) {
            return -1;
        }
        WSSCategory cate = cateRepo.findById(cateId).get();
        for (WSSComponent component : service.listAll(cateId)) {
            component.setCategory(null);
        }
        cateRepo.delete(cate);
        return 1;
    }
}