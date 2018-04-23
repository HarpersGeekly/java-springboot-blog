package com.codeup.blog.springbootblog.Models;

import javax.persistence.*;

@Entity
@Table(name="post_images")
public class PostImage {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String path;

    @ManyToOne
    @JoinColumn(name="post_id")
    private Post post;

    public PostImage(){}

    public PostImage(Long id, String path, Post post) {
        this.id = id;
        this.path = path;
        this.post = post;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
