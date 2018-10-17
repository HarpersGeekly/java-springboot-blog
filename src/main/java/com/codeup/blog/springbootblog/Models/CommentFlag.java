package com.codeup.blog.springbootblog.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "comment_flags")
public class CommentFlag {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private int count;

    @ManyToOne
    @JoinColumn(name = "user_flagger_id")
    private User flagger;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    public CommentFlag(){}

    public CommentFlag(int count, User flagger, Comment comment) {
        this.count = count;
        this.flagger = flagger;
        this.comment = comment;
    }

    @JsonIgnore
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonIgnore
    public User getFlagger() {
        return flagger;
    }

    public void setFlagger(User flagger) {
        this.flagger = flagger;
    }

    @JsonIgnore
    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    @JsonIgnore
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
