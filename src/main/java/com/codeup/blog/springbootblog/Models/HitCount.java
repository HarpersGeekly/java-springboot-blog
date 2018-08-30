package com.codeup.blog.springbootblog.Models;

import javax.persistence.*;

@Entity
@Table(name = "hit_counts")
public class HitCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private int postCount;

    @Column
    private int profileCount;

    @OneToOne
    @JoinColumn(name="post_id")
    private Post post;

    @OneToOne
    @JoinColumn(name="user_id")
    private User user;

    public HitCount(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public int getProfileCount() {
        return profileCount;
    }

    public void setProfileCount(int profileCount) {
        this.profileCount = profileCount;
    }
}
