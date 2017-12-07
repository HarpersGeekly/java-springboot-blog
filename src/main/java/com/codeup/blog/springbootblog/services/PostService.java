package com.codeup.blog.springbootblog.services;
import com.codeup.blog.springbootblog.Models.Post;
import com.codeup.blog.springbootblog.repositories.PostsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by RyanHarper on 11/3/17.
 */

@Service
public class PostService {

    private final PostsRepository postsDao;

    // property for list
    // private List<Post> posts = new ArrayList<>();

    // constructor
    // Now that we have a PostsRepository, we can autowire an instance of it here.
    @Autowired
    public PostService(PostsRepository postsDao) {
        this.postsDao = postsDao;
         //createDummy(); // No longer making dummy hardcode
    }

    // Reminder: There are built in methods in the CRUDRepository:
    // findAll(), findOne(), save(), delete().
    // These are being returned in the following PostService methods:

    public Iterable<Post> findAll() {
        return postsDao.findAll();
    }

    public Post findOne(Long id) {
        // return posts.get((int) (id - 1));
        // PRE-REPOSITORY: why minus 1? Must take into account the +1 I made when I created the post.
        // if I put 2 in the PathVariable, it's still at position 1 in the "database" because it's zero-based indexed.
        return postsDao.findOne(id);
    }

    public Post save(Post post) {
        //PRE-REPOSITORY: think of this as the first post you ever make. size = 0. Then, + 1.
        //post.setId((long) (posts.size() + 1)); // size returns an integer, needs to be long casted. Add 1 to the id so it's not 0.
        //posts.add(post); // add to the List.
        //POST-POSTREPOSITORY:
        return postsDao.save(post);
    }

    public void delete(Long id) {
        postsDao.delete(id);
    }

    public List<Post> searchPostsByKeyword(String term) {
        return postsDao.searchPostsWithKeyword("%" + term + "%");
    }

    public Page<Post> postsByPage(Pageable pageable) {
        return postsDao.postsByPage(pageable);
    }

    // save takes care of insert and update
//    public Post update(Post post) {
//        post.setId((long)(post.getId() - 1), post);
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

}

