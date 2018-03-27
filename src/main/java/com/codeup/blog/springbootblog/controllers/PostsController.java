package com.codeup.blog.springbootblog.controllers;

import com.codeup.blog.springbootblog.Models.Comment;
import com.codeup.blog.springbootblog.Models.Post;
import com.codeup.blog.springbootblog.repositories.CommentsRepository;
import com.codeup.blog.springbootblog.repositories.UsersRepository;
import com.codeup.blog.springbootblog.services.PostService;
import com.codeup.blog.springbootblog.services.UserService;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Date;
//import java.sql.Timestamp;
//import java.time.LocalDateTime;

/**
 * Created by RyanHarper on 11/2/17.
 */

@Controller
public class PostsController {

    // this is the service placeholder that will be final, it'll never change.
    private final PostService postSvc;

    private final UsersRepository usersDao; //making queries to database

    private final UserService userSvc; // using methods in the UserService, like isLoggedIn(), etc.

    private final CommentsRepository commentsDao;

    // Constructor "dependency injection", passing the PostService object into the PostController constructor,
    // everything ties together now. Services + Controller.
    // Autowiring makes it so we don't have to build the object ourselves in the main method of SpringBlogApplication,
    // Spring handles this.

    // Before I made a PostsRepository, I used dummy hard code in the PostService
    // in the form of a List<Post> posts. But now the PostsRepository is being built in the PostsService
    // Reminder: There are built in methods for CRUDRepository:
    // findAll(), findOne(), save(), delete(). These are in the Service

    public PostsController(PostService postSvc,
                           UsersRepository usersDao,
                           UserService userSvc,
                           CommentsRepository commentsDao
 ) {
        this.postSvc = postSvc;
        this.usersDao = usersDao;
        this.userSvc = userSvc;
        this.commentsDao = commentsDao;
    }

    //========================================= SHOW ALL POSTS AND SHOW ONE POST =======================================

    @GetMapping("/posts")
    public String showAllPosts(Model viewModel,
                               @PageableDefault(value = 11, direction = Sort.Direction.DESC) Pageable pageable) {

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

//        List<Post> allPosts = (List<Post>)postSvc.findAll();
//        for (Post post : allPosts) {
//
//        }
//        viewModel.addAttribute("posts", postSvc.findAll());
        viewModel.addAttribute("page", postSvc.postsByPage(pageable));
        return "posts/index";
    }

    @GetMapping("/posts/{id}")
    public String showPostById(@PathVariable Long id, Model viewModel, Comment comment,
                               @PageableDefault(value = 11, sort = "id", direction = Sort.Direction.DESC)
                                       Pageable pageable) {
//        Post post = new Post(1L,"First Title", "First Description");
        Post post = postSvc.findOne(id);
        viewModel.addAttribute("post", post);
        viewModel.addAttribute("isPostOwner", userSvc.isLoggedInAndPostMatchesUser(post.getUser())); // show post edit button
        viewModel.addAttribute("comment", new Comment());
        viewModel.addAttribute("isLoggedIn", userSvc.isLoggedIn());
        viewModel.addAttribute("page", commentsDao.postCommentsByPage(id, pageable));
        viewModel.addAttribute("voteCount", commentsDao.commentVoteCount(comment.getId()));

        return "posts/show";
    }

//    @GetMapping("/posts/{id}.json")
//    public @ResponseBody List<Comment> showAllCommentsInJSONFormat(@PathVariable long id, @PageableDefault(value = 11, sort = "id", direction = Sort.Direction.DESC)
//            Pageable pageable) {
//        return (List<Comment>) commentsDao.postCommentsByPage(id, pageable);
//    }

//    @GetMapping("/posts/{id}")
//    public String viewAllAdsWithAjax() {
//        return "posts/show";
//    }

    // =============================================== CREATE POST =====================================================

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

    // We also need to set the User of the post to the user who is logged in!

    @PostMapping("/posts/create")
    public String createPost(@Valid Post post,
                             Errors validation, Model viewModel) {

        // @Valid Post post now calls @ModelAttribute Post post first/instead and calls the validations!
        // Validation:
        if (validation.hasErrors()) {
            viewModel.addAttribute("errors", validation);
            viewModel.addAttribute("post", post);
            return "/posts/create";
        }

//        XSSPrevent xp = new XSSPrevent();
//        xp.setAsText(post.getTitle());
//        post.setDescription(xp.getAsText());
        // This XSSPrevent isn't allowing me to update my code? What gives?

        post.setUser(userSvc.loggedInUser());
        post.setDate(LocalDateTime.now());
        postSvc.save(post);
        return "redirect:/posts";
    }
//      Now, post will automatically have the title and description that was submitted with the form.
//      This is why it's good to have an empty constructor Post(){} to handle this.

    // =============================================== EDIT POST =======================================================

    @GetMapping("/posts/{id}/edit")
    public String showEditPostForm(@PathVariable Long id, Model viewModel) {
        Post existingPost = postSvc.findOne(id);
        viewModel.addAttribute("post", existingPost);
        return "/posts/edit";
    }

    @PostMapping("/posts/{id}/edit")
    public String update(@PathVariable Long id, @Valid Post post, Errors validation, Model viewModel) {
        // @ModelAttribute = expecting a post object to update/delete @ Valid takes care of this instead.
        // @Valid now calls @ModelAttribute first/instead and calls the validations!
        if (validation.hasErrors()) {
            viewModel.addAttribute("errors", validation);
            viewModel.addAttribute("post", post);
            return "/posts/create";
        }

//        XSSPrevent xp = new XSSPrevent();
//        xp.setAsText(post.getTitle());
//        post.setDescription(xp.getAsText());

        post.setUser(userSvc.loggedInUser());
        post.setId(id);
        postSvc.save(post);
        return "redirect:/posts/{id}";
    }

    // ============================================== DELETE POST ======================================================

    @PostMapping("/posts/{id}/delete")
    public String delete(@PathVariable long id, Post post, Comment comment) {

//  As I delete a post, comments and replies that belong to that post will be deleted too.

        // Set id's:
        comment.setPost(post);
        post.setId(id);

        // Delete the List of comments that belong to the post id
        commentsDao.delete(commentsDao.commentsOnPost(id));
        // Lastly, delete the post.
        postSvc.delete(id);
        return "redirect:/profile";
    }

    // ============================================== SEARCH POST ======================================================

    @GetMapping("/posts/search")
    public String search(@RequestParam String term, Model viewModel) {
        // return "list of posts where title is like ? or description is like ? or username is like ?"
        // Go to PostRepository and make a query method.
        // Go to PostService and implement it there too. Call it here and pass it to the view:
        viewModel.addAttribute("searchedContent", postSvc.searchPostsByKeyword(term));
        return "/posts/search";
    }

    // ========================================== MARKDOWN EDITOR PREVIEW ==============================================

    @GetMapping("/posts/description.json")
    @ResponseBody
    public String showContentInForm(@RequestParam(name = "content") String content) {
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(parser.parse(content));
    }

    //    ==============================================================================================================
    //    =========================================== COMMENT ON A POST ================================================
    //    ==============================================================================================================

    @PostMapping("/posts/{postId}")
    public
//    public @ResponseBody no longer converting the comment into a json string. now returning a template
    String postComment(@PathVariable Long postId, @Valid Comment comment, BindingResult validation, Model viewModel) {

        Post post = postSvc.findOne(postId);
        viewModel.addAttribute("post", post);
        viewModel.addAttribute("isPostOwner", userSvc.isLoggedInAndPostMatchesUser(post.getUser())); // show post edit button
        viewModel.addAttribute("isLoggedIn", userSvc.isLoggedIn());
        viewModel.addAttribute("voteCount", commentsDao.commentVoteCount(comment.getId()));
        viewModel.addAttribute("comment", comment);

        if (validation.hasErrors()) {
            System.out.println("======got to comment validation here=======");
//            viewModel.addAttribute("errors", validation); // By using BindingResult validation instead of Error validation, don't need "errors" attritbute
            System.out.println("====== validation: "+  validation);
            viewModel.addAttribute("comment", comment);
//            validation.rejectValue(
//                    "body",
//                    "comment.body",
//                    "Comments must be at least 2 characters.");

            // return a fragment with only the errors
//            return "/posts/show"; //html page.
            return "fragments/commentError :: ajaxError";
        }

        comment.setPost(post);
        comment.setUser(userSvc.loggedInUser());
        comment.setDate(LocalDateTime.now());
        comment.setVoteCount((long)0);
        commentsDao.save(comment);

//        return comment;
        return "fragments/comments :: ajaxComment"; // By returning this fragment (fragments/comments.html), we get all of our Thymeleaf-operated HTML
    }

//   ============================================ EDIT A COMMENT  ======================================================

    @GetMapping("/posts/{postId}/comment/{commentId}/edit")
    public @ResponseBody Comment editComment(@PathVariable Long postId,
                                             @PathVariable Long commentId, Model viewModel) {
        Comment comment = commentsDao.findOne(commentId);
        viewModel.addAttribute("comment", commentsDao.findOne(commentId));
        return comment;
    }

    @PostMapping("/posts/{postId}/comment/{commentId}/edit")
    public @ResponseBody Comment submitEditedComment(@PathVariable Long postId,
                                                     @PathVariable Long commentId,
                                                     @RequestParam("body") String body, Model viewModel) {
        Comment comment = commentsDao.findOne(commentId);
        comment.setBody(body);
        commentsDao.save(comment);
        viewModel.addAttribute("comment", commentsDao.findOne(commentId));
        return comment;
    }

//   ============================================== DELETE A COMMENT ===================================================
    @PostMapping("/posts/{postId}/comment/{commentId}/delete")
    public @ResponseBody Comment deleteComment(@PathVariable Long postId, @PathVariable Long commentId) {
        Comment comment = commentsDao.findOne(commentId);
        commentsDao.delete(commentId);
        return comment;
    }

    //Before ajax:
//    @PostMapping("/posts/{postId}/comment/{commentId}/delete")
//    public String deleteComment(@PathVariable Long postId, @PathVariable Long commentId) {
//        commentsDao.delete(commentId);
//        return "redirect:/posts/" + postId;
//    }
}
