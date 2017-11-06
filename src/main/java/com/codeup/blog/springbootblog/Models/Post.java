package com.codeup.blog.springbootblog.Models;

/**
 * Created by RyanHarper on 11/3/17.
 */

public class Post {

    private Long id;
    private String title;
    private String description;

    public Post(Long id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    // use on the create action
    public Post(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Post() {}

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
