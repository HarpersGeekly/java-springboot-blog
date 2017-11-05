package com.codeup.blog.springbootblog.controllers;

import com.codeup.blog.springbootblog.Models.Post;
import com.codeup.blog.springbootblog.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;

/**
 * Created by RyanHarper on 11/2/17.
 */

@Controller
public class PostsController {

    // this is the service placeholder that will be final, it'll never change
    private final PostService postSvc;

    // "dependency injection", everything ties together. Services + Controller
    @Autowired
    public PostsController(PostService postSvc) {
        this.postSvc = postSvc;
    }

    @GetMapping("/posts")
    public String showAllPosts(Model viewModel) {

        List<Post> posts = postSvc.findAll();

//        Before making the PostService we had all of this:

//        ArrayList<Post> posts  = new ArrayList<>();

//        Post post1 = new Post(1L,"First Title", "First Description");
//        Post post2 = new Post(2L,"Second Title", "Second Description");
//        Post post3 = new Post(3L,"Third Title", "Third Description");
//        Post post4 = new Post(4L,"Fourth Title", "Fourth Description");

//        posts.add(post1);
//        posts.add(post2);
//        posts.add(post3);
//        posts.add(post4);

        viewModel.addAttribute("posts", posts);
        return "posts/index";
    }

    @GetMapping("/posts/{id}")
    public String showPostById(@PathVariable Long id, Model viewModel) {
//        Post post = new Post(1L,"First Title", "First Description");
        Post post = postSvc.findOne(id);
        viewModel.addAttribute("post", post);
        return "posts/show";
    }

    @GetMapping("/posts/create")
    public String showCreatePostForm(Model viewModel) {
        viewModel.addAttribute("post", new Post());
        return "posts/create";
    }

    @PostMapping("/posts/create")
    public String createPost() {
        return "posts/create";
    }
}
