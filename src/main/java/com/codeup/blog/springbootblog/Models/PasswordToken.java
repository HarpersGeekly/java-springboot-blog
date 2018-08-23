package com.codeup.blog.springbootblog.Models;

import java.time.LocalDateTime;
import javax.persistence.*;

@Entity
@Table(name = "reset_password")
public class PasswordToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String token;

//    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @ManyToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Column
    private LocalDateTime created_on;

    @Column
    private LocalDateTime expires_on;

    public PasswordToken(Long id, String token, User user, LocalDateTime created_on, LocalDateTime expires_on) {
        this.id = id;
        this.token = token;
        this.user = user;
        this.created_on = created_on;
        this.expires_on = expires_on;
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

    public LocalDateTime getCreated_on() {
        return created_on;
    }

    public void setCreated_on(LocalDateTime created_on) {
        this.created_on = created_on;
    }

    public LocalDateTime getExpires_on() {
        return expires_on;
    }

    public void setExpires_on(LocalDateTime expires_on) {
        this.expires_on = expires_on;
    }
}
