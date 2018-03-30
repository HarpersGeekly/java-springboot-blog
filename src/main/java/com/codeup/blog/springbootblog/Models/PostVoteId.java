package com.codeup.blog.springbootblog.Models;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class PostVoteId implements Serializable {

    @Column(name = "post_id")
    private long postId;
    @Column(name = "user_id")
    private long userId;

    public PostVoteId() {
    }

    public PostVoteId(Long postId, Long userId) {
        this.postId = postId;
        this.userId = userId;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
