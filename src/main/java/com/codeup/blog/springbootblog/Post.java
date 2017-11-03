package com.codeup.blog.springbootblog;

/**
 * Created by RyanHarper on 11/3/17.
 */
public class Post {
    private String title;
    private String description;

    public Post(String title, String description) {
        this.title = title;
        this.description = description;
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
}
