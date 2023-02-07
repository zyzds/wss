package zyz.wss.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import zyz.wss.constant.SQLConst;
import zyz.wss.model.HostHolder;
import zyz.wss.model.entity.WSSCategory;
import zyz.wss.model.entity.WSSComponent;
import zyz.wss.model.entity.WSSFile;
import zyz.wss.repository.WSSCategoryRepository;
import zyz.wss.repository.WSSComponentRepository;
import zyz.wss.service.WSSComponentService;

@Service
public class WSSComponentServiceImpl implements WSSComponentService {
    private static final Logger log = LoggerFactory.getLogger(WSSComponentServiceImpl.class);

    @Autowired
    private HostHolder holder;

    @Autowired
    private WSSComponentRepository compRepo;

    @Autowired
    private WSSCategoryRepository wssCategoryRepository;

    @Override
    public boolean checkNameRepeat(String parentId, String name, String type) {
        Assert.hasText(name, "name cannot be null");
        Assert.hasText(type, "type cannot be null");

        WSSComponent component = new WSSComponent();
        if (StringUtils.hasText(parentId)) {
            WSSComponent parent = new WSSComponent();
            parent.setId(parentId);
            component.setParent(parent);
        }
        
        component.setName(name).setType(type).setOwner(holder.get());
        List<WSSComponent> components = listAll(component, false, false);

        return !components.isEmpty();
    }

    @Override
    public void createComponent(String parentId, String name, String type) {
        /* Assert.hasText(parentId, "parentId cannot be null");
        Assert.hasText(name, "name cannot be null");
        Assert.hasText(type, "type cannot be null");

        if (component instanceof WSSFolder) {
            wssFolderRepository.save((WSSFolder) component);
        } */
    }

    @Override
    public int deleteComponent(WSSComponent component) {
        if (component == null || component.getId() == null) {
            return 0;
        }
        int row = 0;
        Optional<WSSComponent> opt = compRepo.findById(component.getId());
        Assert.isTrue(opt.isPresent(), "id is not valid");
        component = opt.get();
        component.setDiscard(true);
        compRepo.save(component);

        WSSComponent example = new WSSComponent();
        example.setParent(component).setOwner(holder.get());
        List<WSSComponent> children = listAll(example, false, true);
        for (WSSComponent wssComponent : children) {
            row += deleteComponent(wssComponent);
        }
        return ++row;
    }

    @Override
    public int deleteAll(List<WSSComponent> components) {
        int row = 0;
        for (WSSComponent component : components) {
            row += deleteComponent(component);
        }
        return row;
    }

    @Override
    public void updateComponent(WSSComponent component) {
        compRepo.save(component);
    }

    public List<WSSComponent> listAll(Example<WSSComponent> example, Sort sort) {
        return compRepo.findAll(example, sort);
    }

    @Override
    public List<WSSComponent> listAll(WSSComponent component, boolean parentIgnore, boolean typeIgnore) {
        return listAll(component, null, null, parentIgnore, typeIgnore);
    }

    @Override
    public List<WSSComponent> listAll(WSSComponent component, String orderBy, String orderByField, boolean parentIgnore, boolean typeIgnore) {
        WSSComponent parent = component.getParent();
        if (parent != null) {
            Assert.hasText(parent.getId(), "parent's id cannnot be none");

            Optional<WSSComponent> opt = compRepo.findById(parent.getId());
            Assert.isTrue(opt.isPresent(), "no such parent");
            parent = opt.get();
            component.setParent(parent);
        }
        
        Sort sort = null;
        if (!StringUtils.isEmpty(orderBy) && !StringUtils.isEmpty(orderByField)) {
            orderBy = orderBy.toUpperCase();
            sort = SQLConst.ASC.equals(orderBy) ? Sort.by(orderByField).ascending()
                    : (SQLConst.DESC.equals(orderBy) ? Sort.by(orderByField).descending() : null);
        } else {
            sort = Sort.by("type", "name").ascending();
        }
        return listAll(buildExample(component, false, parentIgnore, typeIgnore), sort);
    }

    @Override
    public List<WSSComponent> listAll(Integer categoryId) {
        List<WSSComponent> list = new ArrayList<>();
        if (categoryId == null) {
            return list;
        }
        Optional<WSSCategory> opt = wssCategoryRepository.findById(categoryId);
        if (!opt.isPresent()) {
            return list;
        }
        WSSCategory category = opt.get();
        WSSComponent component = new WSSComponent();
        component.setCategory(category).setOwner(holder.get());
        return listAll(buildExample(component, false, true, true), Sort.by("type", "name").ascending());
    }

    @Override
    public List<WSSComponent> searchComponents(String keyword) {
        List<WSSComponent> list = new ArrayList<>();
        if (StringUtils.isEmpty(keyword)) {
            return list;
        }
        WSSComponent component = new WSSComponent();
        component.setName(keyword).setOwner(holder.get());
        return listAll(buildExample(component, true, true, true), Sort.by("type", "name").ascending());
    }

    public Example<WSSComponent> buildExample(WSSComponent component, boolean fuzzy, boolean parentIgnore, boolean typeIgnore) {
        ExampleMatcher matcher = ExampleMatcher.matching().withIncludeNullValues()
                .withIgnorePaths("id", "updateTime", "file")
                .withMatcher("name", fuzzy ? ExampleMatcher.GenericPropertyMatchers.contains()
                        : ExampleMatcher.GenericPropertyMatchers.exact());

        matcher = component.getName() == null ? matcher.withIgnorePaths("name") : matcher;
        matcher = component.getCategory() == null ? matcher.withIgnorePaths("category") : matcher;
        matcher = component.getOwner() == null ? matcher.withIgnorePaths("owner") : matcher;
        matcher = parentIgnore ? matcher.withIgnorePaths("parent") : matcher;
        matcher = typeIgnore ? matcher.withIgnorePaths("type") : matcher;
        Example<WSSComponent> example = Example.of(component, matcher);
        return example;
    }

    public boolean isOwner(WSSComponent component) {
        /* WSSComponent c = null;
        if (component instanceof WSSFolder) {
            Optional<WSSFolder> opt = wssFolderRepository.findById(component.getId());
            if (opt.isPresent()) {
                c = opt.get();
            }
        } else if (component instanceof WSSUserFile) {
            Optional<WSSUserFile> opt = wssUserFileRepository.findById(component.getId());
            if (opt.isPresent()) {
                c = opt.get();
            }
        }
       
        User u = c == null ? null : c.getOwner();
        if (u != null && u.getId() == holder.get().getId()) {
            return true;
        } */
        return false;
    }

    @Override
    public WSSComponent listOne(String id, String type) {
        Assert.hasText(id, "parentId cannot be null");
        Assert.hasText(type, "type cannot be null");
        
        return compRepo.findByIdAndType(id, type);
    }

    @Override
    public void download(OutputStream ops, String id) {
        if (ops == null || StringUtils.isEmpty(id)) {
            return;
        }

        Optional<WSSComponent> opt = compRepo.findById(id);
        if (opt.isPresent()) {
            WSSFile file = opt.get().getFile();
            if (file != null) {
                File f = new File(file.getLocation());
                Assert.isTrue(!f.exists(), "文件路径无效");
                try (FileInputStream fis = new FileInputStream(f);) {
                    byte[] bs = new byte[1024];
                    while (fis.read(bs) != -1) {
                        ops.write(bs);
                    }
                } catch (IOException e) {
                    log.error("文件下载读取错误", e);
                }
            }
            
            
        }
    }
}