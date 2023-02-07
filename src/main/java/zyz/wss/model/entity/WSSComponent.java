package zyz.wss.model.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.util.Assert;

@Entity
public class WSSComponent implements Cloneable {

    @Id
    @GenericGenerator(name = "system-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "system-uuid")
    String id;
    String name;
    Date updateTime;
    @Type(type = "yes_no")
    Boolean discard = false;
    @ManyToOne
    private User owner;
    @ManyToOne
    private WSSComponent parent;
    @ManyToOne
    private WSSFile file;
    @ManyToOne
    private WSSCategory category;
    private String type;

    public String getId() {
        return id;
    }

    public WSSComponent setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public WSSComponent setName(String name) {
        this.name = name;
        return this;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public WSSComponent setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public Boolean getDiscard() {
        return discard;
    }

    public WSSComponent setDiscard(Boolean discard) {
        this.discard = discard;
        return this;
    }

    public Long getSize() {
        Assert.notNull(this.file, "userfile's relate file cannot be null");
        return this.file.getSize();
    }

    public User getOwner() {
        return owner;
    }

    public WSSComponent setOwner(User owner) {
        this.owner = owner;
        return this;
    }

    public WSSComponent getParent() {
        return parent;
    }

    public WSSComponent setParent(WSSComponent parent) {
        this.parent = parent;
        return this;
    }

    public WSSFile getFile() {
        return file;
    }

    public WSSComponent setFile(WSSFile file) {
        this.file = file;
        return this;
    }

    public WSSCategory getCategory() {
        return category;
    }

    public WSSComponent setCategory(WSSCategory category) {
        this.category = category;
        return this;
    }

    public String getType() {
        return type;
    }

    public WSSComponent setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public WSSComponent clone() {
        WSSComponent target = null;
        try {
            target = (WSSComponent) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return target;
    }
}
