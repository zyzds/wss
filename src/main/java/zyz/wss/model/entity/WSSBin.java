package zyz.wss.model.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class WSSBin {
    @Id
    @GenericGenerator(name = "system-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "system-uuid")
    String id;
    @OneToOne
    private WSSComponent component;
    private Date deleteTime;
    private Integer remain;
    @ManyToOne
    private User owner;

    

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public WSSComponent getComponent() {
        return component;
    }

    public void setComponent(WSSComponent component) {
        this.component = component;
    }

    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }

    public Integer getRemain() {
        return remain;
    }

    public void setRemain(Integer remain) {
        this.remain = remain;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public WSSBin(WSSComponent component, Date deleteTime, Integer remain, User owner) {
        this.component = component;
        this.deleteTime = deleteTime;
        this.remain = remain;
        this.owner = owner;
    }

    public WSSBin() {
    }
}