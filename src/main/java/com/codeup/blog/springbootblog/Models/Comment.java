package com.codeup.blog.springbootblog.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

//    @Column
//    private Long parent_comment_id;
    // all replies are comments, but with a parent_id of the comment they're replying to.

    @Column(columnDefinition = "TEXT", length = 2000, nullable = false) // column, text for more, not-null
    @NotBlank(message="Comments cannot be empty.")
    @Size(min = 2, message="Comments must be more than 2 characters.")
    private String body;

    @ManyToOne // many comments will belong to one Post
//    @JsonManagedReference
    @JsonIgnore
    private Post post; // this is the mappedBy "post"

    @ManyToOne // many comments will belong to one User
//    @JsonManagedReference
    @JsonIgnore
    private User user;

    @Column(name = "CREATED_DATE")
    private LocalDateTime date;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentVote> commentVotes;

    public Comment(){}

    public Comment(String body, Post post, User user, LocalDateTime date, List<CommentVote> commentVotes) {
        this.body = body;
        this.post = post;
        this.user = user;
        this.date = date;
        this.commentVotes = commentVotes;
    }

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

    public List<CommentVote> getCommentVotes() {
        return commentVotes;
    }

    public void setCommentVotes(List<CommentVote> commentVotes) {
        this.commentVotes = commentVotes;
    }

//    public List<Reply> getReplies() {
//        return replies;
//    }
//
//    public void setReplies(List<Reply> replies) {
//        this.replies = replies;
//    }
}
