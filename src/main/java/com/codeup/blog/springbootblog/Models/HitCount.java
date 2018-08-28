package com.codeup.blog.springbootblog.Models;

import javax.persistence.*;

@Entity
@Table(name = "hit_counts")
public class HitCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private int count;

    @OneToOne
    @JoinColumn(name="post_id")
    private Post post;

    public HitCount(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
