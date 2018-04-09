package com.codeup.blog.springbootblog.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity @Table(name = "comments_votes")
public class CommentVote {

    @EmbeddedId
    private CommentVoteId id;
    // "this class needs to exist and needs an id"
    // but it doesn't show up in the db, there's no incrementing, and it doesn't change the db at all

    @ManyToOne @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne @JoinColumn(name = "comment_id", insertable = false, updatable = false)
    private Comment comment;

    private int type;

    public CommentVote(){}

    // these four things embody a CommentVote object when it's created: an embeddable id,
    // the Comment it belongs to,
    // the User that clicked it,
    // and what type of vote it was up or down...
    public CommentVote(User user, Comment comment, int type) {
        this.id = new CommentVoteId(comment.getId(), user.getId());
        this.user = user;
        this.comment = comment;
        this.type = type;
    }

    // why do I have to add JsonIgnore on the getters?
    @JsonIgnore
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @JsonIgnore
    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    @JsonIgnore
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @JsonIgnore
    public CommentVoteId getId() {
        return id;
    }

    public void setId(CommentVoteId id) {
        this.id = id;
    }
}
