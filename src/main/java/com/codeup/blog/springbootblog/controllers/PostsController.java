package com.codeup.blog.springbootblog.controllers;

import com.codeup.blog.springbootblog.Models.Post;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.ArrayList;

/**
 * Created by RyanHarper on 11/2/17.
 */

@Controller
public class PostsController {

    @GetMapping("/posts")
    public String showPosts(Model viewModel) {

        Post post1 = new Post("First Title", "First Description");
        Post post2 = new Post("Second Title", "Second Description");
        Post post3 = new Post("Third Title", "Third Description");
        Post post4 = new Post("Fourth Title", "Fourth Description");

        ArrayList<Post> posts  = new ArrayList<>();

        posts.add(post1);
        posts.add(post2);
        posts.add(post3);
        posts.add(post4);

        viewModel.addAttribute("posts", posts);
        return "posts/index";
    }

    @GetMapping("/posts/{id}")
    public String showPostById(@PathVariable Long id) {

        

        return "posts/show";
    }

    @GetMapping("/posts/create")
    @ResponseBody
    public String showCreatePostForm() {
        return "view the form for creating a post";
    }

    @PostMapping("/posts/create")
    @ResponseBody
    public String createPost() {
        return "create a new post";
    }
}
