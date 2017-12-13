package com.codeup.blog.springbootblog.Models;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Created by RyanHarper on 12/13/17.
 */
@Entity // annotation saying "will be a table".
@Table(name = "replies") // name of database table
public class Reply {

    @Id // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment
    private Long id;

    @Column(columnDefinition = "TEXT", length = 2000, nullable = false) // column, text for more, not-null
    @NotBlank(message="Comments cannot be empty.")
    @Size(min = 2, message="Comments must be more than 2 characters.")
    private String body;

    @Column(nullable = true) /* null vs setVoteCount(0) ???  */
    private long voteCount;

    @ManyToOne // many replies will belong to one User
    private User user;

    @ManyToOne // many replies will belong to one Comment
    private Comment comment;

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

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
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
}
