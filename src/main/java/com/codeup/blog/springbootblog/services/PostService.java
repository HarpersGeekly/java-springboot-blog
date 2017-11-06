package com.codeup.blog.springbootblog.services;
import com.codeup.blog.springbootblog.Models.Post;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by RyanHarper on 11/3/17.
 */


@Service("PostService")
public class PostService {

    //property for list
    private List<Post> posts = new ArrayList<>();

    // constructor
    public PostService() {
        createDummy();
    }

    public List<Post> findAll() {
        return posts;
    }

    public Post findOne(Long id) {
        return posts.get((int) (id - 1));
    }

    public Post savePost(Post post) {
        post.setId((long) (posts.size() + 1)); // size returns an integer, needs to be Long casted.
        posts.add(post);
        return post;
    }

    private void createDummy() {
        this.savePost(new Post(
                1L,
                "First Title",
                "First Description"
        ));
        this.savePost(new Post(
                2L,
                "Second Title",
                "Second Description"
        ));
        this.savePost(new Post(
                3L,
                "Third Title",
                "Third Description"
        ));
        this.savePost(new Post(
                4L,
                "Fourth Title",
                "Fourth Description"
        ));
//        this.savePost(new Post(
//                5L,
//                "5 Title",
//                "5 Description"
//        ));
//        this.savePost(new Post(
//                6L,
//                "6 Title",
//                "6 Description"
//        ));
//        this.savePost(new Post(
//                7L,
//                "7 Title",
//                "7 Description"
//        ));
//        this.savePost(new Post(
//                8L,
//                "8 Title",
//                "8 Description"
//        ));
//        this.savePost(new Post(
//                9L,
//                "5 Title",
//                "5 Description"
//        ));
//        this.savePost(new Post(
//                10L,
//                "6 Title",
//                "6 Description"
//        ));
//        this.savePost(new Post(
//                11L,
//                "7 Title",
//                "7 Description"
//        ));
//        this.savePost(new Post(
//                12L,
//                "12 Title",
//                "12 Description"
//        ));
    }
}
