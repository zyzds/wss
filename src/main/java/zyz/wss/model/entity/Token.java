package zyz.wss.model.entity;

import org.hibernate.annotations.GenericGenerator;

import zyz.wss.util.WssUtil;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
public class Token {
    public static final int EXPIRE = 30;   //Token过期时间（天）

    @Id
    @GenericGenerator(name = "system-uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "system-uuid")
    private String id;
    @ManyToOne
    private User user;
    private Date loginTime;
    private String loginIP;
    /**
     * 
     * @param user  登录用户
     * @param loginTime 登陆时间
     * @param loginIP 登录IP
     */
    public Token(User user, Date loginTime, String loginIP) {
        this.user = user;
        this.loginTime = loginTime;
        this.loginIP = loginIP;
    }

    public Token() {}
    public boolean isExpire() {
        if (this.loginTime == null) {
            return true;
        }
        return (WssUtil.dateToLocalDateTimeConverter(this.getLoginTime())
            .plusDays(Token.EXPIRE).isBefore(LocalDateTime.now()));
    }

    //getter and setter...
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public String getLoginIP() {
        return loginIP;
    }

    public void setLoginIP(String loginIP) {
        this.loginIP = loginIP;
    }
}
