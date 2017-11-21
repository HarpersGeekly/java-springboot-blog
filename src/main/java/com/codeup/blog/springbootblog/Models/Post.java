package com.codeup.blog.springbootblog.Models;

import org.hibernate.validator.constraints.NotBlank;
import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
//import java.util.Date;
//import java.sql.Timestamp;
//import java.time.LocalDateTime;

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
    @NotBlank(message="Title cannot be empty")
    private String title;

    @Column(columnDefinition = "TEXT", length = 2000, nullable = false) // column, text for more, not-null
    @NotBlank(message="Description cannot be empty :/")
    @Size(min = 5, message="Description must be at least 5 characters.")
    private String description;

    @ManyToOne // many posts can belong to one user.
    // will define the foreign key. This class represents the post table and we need a reference to the user
    private User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post") // One post will have many comments
    private List<Comment> comments;

    @Column(name = "CREATED_DATE")
    private LocalDateTime date;

    //use when the post is retrieved from the database.
    public Post(Long id, String title, String description, User user, String image, LocalDateTime date, List<Comment> comments) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.user = user; // this gives me access to properties from user, user.getUsername()
        this.date = date;
        this.comments = comments;
//        this.image = image; // may not need this here.
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public List<Comment> getComment() {
        return comments;
    }

    public void setComment(List<Comment> comments) {
        this.comments = comments;
    }

//    public String getImage() {
//        return image;
//    }

//    public void setImage(String image) {
//        this.image = image;
//    }
}
