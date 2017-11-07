package com.codeup.blog.springbootblog.services;
import com.codeup.blog.springbootblog.Models.Post;
import com.codeup.blog.springbootblog.repositories.PostsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by RyanHarper on 11/3/17.
 */


@Service("PostService")
public class PostService {


    private final PostsRepository postsDao;

    //property for list
//    private List<Post> posts = new ArrayList<>();

    // constructor
    // Now that we have a PostsRepository, we can autowire an instance of it here.
    @Autowired
    public PostService(PostsRepository postsDao) {
        this.postsDao = postsDao;
//        createDummy(); // No longer making dummy hardcode
    }

//    public List<Post> findAll() {
//        return posts;
//    }

//    public Post findOne(Long id) {
//        return posts.get((int) (id - 1)); // why minus 1? Must take into account the +1 I made when I created the post.
//        //if I put 2 in the PathVariable, it's still at position 1 in the "database" because it's zero-based indexed.
//    }

    // this is only used for the Dummy List here in this class.
//    public Post savePost(Post post) { // think of this as the first post you ever make. size = 0. Then, + 1.
//        post.setId((long) (posts.size() + 1)); // size returns an integer, needs to be long casted. Add 1 to the id so it's not 0.
//        posts.add(post); // add to the List.
//        return post;
//    }

//    public Post updatePost(Post post) {
//        post.setId((long)(post.getId() - 1), post);
//        return post;
//    }
//
//    public Post delete(Post post) {
//        return post;
//    }

//    private void createDummy() {
//        savePost(new Post( // uses the old constructor with just the two parameters...
////                1L, you don't need to set ID here. savePost handles it. and notice how /posts/1/edit will take you to the first post.
//                "1 Title",
//                "1 Description"
//        ));
//        savePost(new Post(
////                2L,
//                "2 Title",
//                "2 Description"
//        ));
//        savePost(new Post( // uses the old constructor with all three parameters...
//                3L,
//                "3 Title",
//                "3 Description"
//        ));
//        savePost(new Post(
//                4L,
//                "4 Title",
//                "4 Description"
//        ));
//        savePost(new Post(
//                5L,
//                "5 Title",
//                "5 Description"
//        ));
//        savePost(new Post(
//                6L,
//                "6 Title",
//                "6 Description"
//        ));
//        savePost(new Post(
//                7L,
//                "7 Title",
//                "7 Description"
//        ));
}

