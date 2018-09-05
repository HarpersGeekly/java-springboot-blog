package com.codeup.blog.springbootblog.Models;

import javax.persistence.*;

@Entity
@Table(name = "user_roles")
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "role")
    private String role;

    public UserRole(long id, long userId, String role) {
        this.id = id;
        this.userId = userId;
        this.role = role;
    }

    public UserRole(long userId, String role) {
        this.userId = userId;
        this.role = role;
    }

    // Default constructor...
    public UserRole(){}

    // Getters and setters...
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public static UserRole admin(User user) {
        return new UserRole(user.getId(), "ROLE_ADMIN");
    }
    public static UserRole editor(User user) {
        return new UserRole(user.getId(), "ROLE_EDITOR");
    }
    public static UserRole user(User user) {
        return new UserRole(user.getId(), "ROLE_USER");
    }


}
