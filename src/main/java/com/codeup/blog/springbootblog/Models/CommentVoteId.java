package com.codeup.blog.springbootblog.Models;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class CommentVoteId implements Serializable {

// To serialize an object means to convert its state to a byte stream so that the byte stream
// can be reverted back into a copy of the object.

    @Column(name = "comment_id")
    private long commentId;

    @Column(name = "user_id")
    private long userId;

    public CommentVoteId(){}

    public CommentVoteId(long commentId, long userId) {
        this.commentId = commentId;
        this.userId = userId;
    }

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
