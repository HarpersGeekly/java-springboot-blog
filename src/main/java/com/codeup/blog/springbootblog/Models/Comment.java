package com.codeup.blog.springbootblog.Models;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Created by RyanHarper on 11/20/17.
 */

@Entity // annotation saying "will be a table".
@Table(name = "comments") // name of database table
public class Comment {

    @Id // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment
    private Long id;

    @Column(columnDefinition = "TEXT", length = 2000, nullable = false) // column, text for more, not-null
    @NotBlank(message="Comment cannot be empty :/")
    @Size(min = 5, message="Comment must be at least 5 characters.")
    private String body;

    @Column
    private int rating;

    @ManyToOne // many comments will belong to one Post
    private Post post;

    @ManyToOne // many comments will belong to one User
    private User user;

    @Column(name = "CREATED_DATE")
    private LocalDateTime date;

    public Comment() {}
//    public Comment(Long id, String body, Post post, User user) {
//        this.id = id;
//        this.body = body;
//        this.post = post;
//        this.user = user;
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
