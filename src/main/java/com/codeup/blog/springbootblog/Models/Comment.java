package com.codeup.blog.springbootblog.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

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
    @NotBlank(message="Comments cannot be empty.")
    @Size(min = 2, message="Comments must be more than 2 characters.")
    private String body;

    @Column(nullable = true) /* null vs setVoteCount(0) ???  */
    private long voteCount;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "comment") //if I delete comments, it will delete-cascade the relationship to replies as well.
//    @JsonBackReference
    private List<Reply> replies;

    @ManyToOne // many comments will belong to one Post
//    @JsonManagedReference
    private Post post; // this is the mappedBy "post"

    @ManyToOne // many comments will belong to one User
//    @JsonManagedReference
    private User user;

    @Column(name = "CREATED_DATE")
    private LocalDateTime date;

    public boolean isOwnedBy(long userId) {
        return getUser() != null && getUser().getId().equals(userId);
    }

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

    public long getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(long voteCount) {
        this.voteCount = voteCount;
    }

    public List<Reply> getReplies() {
        return replies;
    }

    public void setReplies(List<Reply> replies) {
        this.replies = replies;
    }
}
