package com.codeup.blog.springbootblog.Models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "reset_password")
public class PasswordToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Column
    private Date created_on;

    public PasswordToken(Long id, String token, User user, Date created_on) {
        this.id = id;
        this.token = token;
        this.user = user;
        this.created_on = created_on;
    }

    public PasswordToken(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreated_on() {
        return created_on;
    }

    public void setCreated_on(Date created_on) {
        this.created_on = created_on;
    }


//    public boolean isExpired(Date created_on) {
//
//        long created_on =
//        long tenAgo = System.currentTimeMillis() - 10 * 60 * 1000;
//
//        if (created_on < tenAgo) {
//            return true;
//        } else {
//            return false;
//        }
//
//    }
}
