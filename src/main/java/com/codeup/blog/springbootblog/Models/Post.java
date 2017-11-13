package com.codeup.blog.springbootblog.Models;

import javax.persistence.*;

/**
 * Created by RyanHarper on 11/3/17.
 */

@Entity // annotation saying "will be a table".
@Table(name = "posts") // name of database table
public class Post {

    @Id // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment
    private Long id;

    @Column(nullable = false, length = 100) // column on table, not-null
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false) // column, text for more, not-null
    private String description;

    @ManyToOne // many posts can belong to one user.
    private User user;

    //use when the post is retrieved from the database.
    public Post(Long id, String title, String description, User user) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.user = user; // this gives me access to properties from user, user.getUsername()
    }

    //use on the create form action with Model binding.
    public Post(String title, String description) {
        this.title = title;
        this.description = description;
    }

    //use for Spring magic.
    public Post() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUsername() {
        return user.getUsername();
    }
}
