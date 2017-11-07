package com.codeup.blog.springbootblog.controllers;

import com.codeup.blog.springbootblog.Models.Post;
import com.codeup.blog.springbootblog.repositories.PostsRepository;
import com.codeup.blog.springbootblog.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;

/**
 * Created by RyanHarper on 11/2/17.
 */

@Controller
public class PostsController {

    // this is the service placeholder that will be final, it'll never change.
    private final PostService postSvc;
    //this is the repository placeholder;
    private final PostsRepository postsDao;

    // Constructor "dependency injection", passing the PostService object into the PostController constructor,
    // everything ties together now. Services + Controller.
    // Autowiring makes it so we don't have to build the object ourselves in the main method of SpringBlogApplication,
    // Spring handles this.

    // Before I made a PostsRepository, I used dummy hard code in the PostService in the form of a List<Post> posts.
    // But, now I have a PostsRepository that extends CRUD for use on the Database.
    // I'll name it postsDao, because it acts similarly to a DAO talking to the database.
    // Now, in PostsController, I'll get rid of all references to the PostService postSvc,
    // but I'll still keep it handy in the constructor..
    // Reminder: There are built in methods for CRUDRepository we will use in the PostsController:
    // findAll(), findOne(), save(), delete().
    @Autowired
    public PostsController(PostService postSvc, PostsRepository postsDao) {
        this.postSvc = postSvc;
        this.postsDao = postsDao;
    }

    //========================================= SHOW ALL POSTS AND SHOW ONE POST =======================================

    @GetMapping("/posts")
    public String showAllPosts(Model viewModel) {

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
//        List<Post> posts = postSvc.findAll();

        viewModel.addAttribute("posts", postsDao.findAll());
        return "posts/index";
    }

    @GetMapping("/posts/{id}")
    public String showPostById(@PathVariable Long id, Model viewModel) {
//        Post post = new Post(1L,"First Title", "First Description");
//        Post post = postSvc.findOne(id);
        Post post = postsDao.findOne(id);
        viewModel.addAttribute("post", post);
        return "posts/show";
    }

    // =============================================== CREATE FORM =====================================================

    @GetMapping("/posts/create")
    public String showCreatePostForm(Model viewModel) {
        viewModel.addAttribute("post", new Post());
        return "posts/create";
    }
//      Essentially we see a blank form when we load the page.
//      We are displaying the 'title' and 'description' properties of a new post, which doesn't
//      have any values set for these properties.
//      We have to make it have an empty object to fill.
//      So we need to first prepare the form to have a Post object to be submitted.
//      Therefore on the create.html we use a thymeleaf object <form th:object="${post}">, and the thymeleaf fields:
//      th:field:*{title}, th:field:*{description}. the * means post.title, post.description
//      and we get the "post" from:

    @PostMapping("/posts/create")
    public String createPost(@ModelAttribute Post post) {
//        postSvc.save(post);
        postsDao.save(post);
        return "redirect:/posts";
    }
//      Now, post will automatically have the title and description that was submitted with the form.
//      This is why it's good to have an empty constructor Post(){} to handle this.

    // =============================================== EDIT POST =======================================================

    @GetMapping("/posts/{id}/edit")
    public String showEditPostForm(@PathVariable Long id, @ModelAttribute Post post, Model viewModel) {
//        Post existingPost = postSvc.findOne(id);
        Post existingPost = postsDao.findOne(id);
        viewModel.addAttribute("post", existingPost);
        return "/posts/edit";
    }

    @PostMapping("/posts/{id}/edit")
    public String update(Post post, Model viewModel) {
        viewModel.addAttribute("post", post);
//        postSvc.save(post);
        postsDao.save(post);
        return "redirect:/posts/{id}";
    }

    // ============================================== DELETE POST ======================================================

    @PostMapping("/posts/delete")
    public String delete(@ModelAttribute Post post) { // @ModelAttribute = expecting a post object to delete
        postsDao.delete(postsDao.findOne(post.getId()));
        return "redirect:/posts";
    }
}
