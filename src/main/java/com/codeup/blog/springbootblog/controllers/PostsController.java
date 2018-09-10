package com.codeup.blog.springbootblog.controllers;

import com.codeup.blog.springbootblog.Models.*;
import com.codeup.blog.springbootblog.repositories.CategoriesRepository;
import com.codeup.blog.springbootblog.repositories.HitCountsRepository;
import com.codeup.blog.springbootblog.repositories.RolesRepository;
import com.codeup.blog.springbootblog.repositories.UsersRepository;
import com.codeup.blog.springbootblog.services.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by RyanHarper on 11/2/17.
 */

@Controller
public class PostsController {

    // this is the service placeholder that will be final, it'll never change.
    private final PostService postSvc;

    private final UsersRepository usersDao; //making queries to database
    private final RolesRepository rolesDao;


    private final UserService userSvc; //

    private final CommentService commentSvc;
    private static final int MAX_COMMENT_LEVEL = 5;

    private final CategoriesRepository categoriesDao;

    private final HitCountsRepository hitCountsDao;

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
                           CommentService commentSvc,
                           CategoriesRepository categoriesDao,
                           HitCountsRepository hitCountsDao,
                           RolesRepository rolesDao
    ) {
        this.postSvc = postSvc;
        this.usersDao = usersDao;
        this.userSvc = userSvc;
        this.commentSvc = commentSvc;
        this.categoriesDao = categoriesDao;
        this.hitCountsDao = hitCountsDao;
        this.rolesDao = rolesDao;
    }

//================================================ ALL POSTS ==================================================== /posts
//======================================================================================================================

    @GetMapping("/posts")
    public String showAllPosts(Model viewModel) {
//                               @PageableDefault(value = 7, direction = Sort.Direction.DESC) Pageable pageable) {

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
//        viewModel.addAttribute("page", postSvc.postsByPage(pageable));

        if(userSvc.loggedInUser() != null) {
            System.out.println("");
            User loggedInUser = userSvc.loggedInUser();
            List<String> myRoles = rolesDao.ofUserWith(loggedInUser.getUsername());
            for (String role : myRoles) {
                if (role.equals("ROLE_ADMIN")) {
                    viewModel.addAttribute("isLoggedInUserAdmin", role);
                }
            }
        }

        viewModel.addAttribute("posts", postSvc.postsByResultSetIndexPage());
        viewModel.addAttribute("categories", categoriesDao.findAll());
        viewModel.addAttribute("mostCommentedPosts", postSvc.popularPostsByCommentActivity());
        viewModel.addAttribute("mostLikedPosts", postSvc.popularPostsByLikes());
        viewModel.addAttribute("mostViewedPosts", postSvc.popularPostsByViews());
        viewModel.addAttribute("popularUsers", usersDao.popularUsersByKarma());
        viewModel.addAttribute("karmas", usersDao.popularUsersKarma());
        return "posts/index";
    }

    @GetMapping("/posts/archived")
    public String showArchivedPosts(Model viewModel) {
        viewModel.addAttribute("posts", postSvc.findAll());
        return "posts/archivedPosts";
    }

//============================================= INFINITE SCROLL AJAX POSTS =============================================
//======================================================================================================================

    @GetMapping("/posts/retrieveMorePosts/{limit}/{batch}")
    public String retrieveMorePosts(Model viewModel, @PathVariable int limit, @PathVariable int batch) {
//                                    @PageableDefault(value = 2,
//                                            direction = Sort.Direction.DESC)
//                                            Pageable pageable) {

        List<Post> posts = postSvc.postsByResultSet();

        if (posts.size() < (limit * batch) || posts.size() < (limit * batch) + limit) {
            viewModel.addAttribute("nextResultSet", posts.subList(limit * batch, posts.size()));
        } else {
            viewModel.addAttribute("nextResultSet", posts.subList(limit * batch, (limit * batch) + limit));
        }

        return "fragments/posts :: ajaxPosts";
    }

//================================================= SHOW POST ====================================== /posts/{id}/{title}
//======================================================================================================================



    @GetMapping("/posts/{id}/{title}")
    public String showPostById(@PathVariable Long id, Model viewModel, Comment comment) {
//                               @PageableDefault(value = 11, sort = "id", direction = Sort.Direction.DESC)
//                                       Pageable pageable) {
//        Post post = new Post(1L,"First Title", "First Description");
//        viewModel.addAttribute("page", commentSvc.postCommentsByPage(id, pageable));

        System.out.println("get to show.html controller");

        Post post = postSvc.findOne(id);
        User postOwner = post.getUser();
        User loggedInUser = userSvc.loggedInUser();
//        if(userSvc.loggedInUser() != null) {
//            System.out.println("");
//            List<String> myRoles = rolesDao.ofUserWith(loggedInUser.getUsername());
//            for (String role : myRoles) {
//                if (role.equals("ROLE_ADMIN")) {
//                    viewModel.addAttribute("isLoggedInUserAdmin", role);
//                }
//            }
//        }
        List<Comment> comments = commentSvc.commentsOnPost(id);
        List<Category> categories = (List<Category>) categoriesDao.findAll();

        HitCount postHitCount = post.getHitCount();
        if(postHitCount == null) {
            HitCount newHitCount = new HitCount();
            newHitCount.setPost(post);
            postHitCount = newHitCount;
        }
        viewModel.addAttribute("count", postHitCount.getPostCount());

        postHitCount.setPostCount(postHitCount.getPostCount() + 1);
        hitCountsDao.save(postHitCount);

        boolean isLoggedIn = userSvc.isLoggedIn();
        boolean isPostOwner = userSvc.isLoggedInAndPostMatchesUser(post.getUser());
        boolean isParentComment = comment.isParentComment(comment);
        boolean isDisabled = post.isDisabled();

        viewModel.addAttribute("post", post);
        viewModel.addAttribute("postOwner", postOwner);
        viewModel.addAttribute("loggedInUser", loggedInUser);
        viewModel.addAttribute("comment", comment);
        viewModel.addAttribute("comments", comments);
        viewModel.addAttribute("categories", categories);
        viewModel.addAttribute("isLoggedIn", isLoggedIn);
        viewModel.addAttribute("isPostOwner", isPostOwner);
        viewModel.addAttribute("isParentComment", isParentComment);
        viewModel.addAttribute("isDisabled", isDisabled);

        if (loggedInUser != null) {
            PostVote vote = post.getVoteFrom(loggedInUser);
            viewModel.addAttribute("upvote", vote != null && vote.isUpvote());
            viewModel.addAttribute("downvote", vote != null && vote.isDownVote());
        }

        return "posts/show";
    }

//================================================ CREATE POST =========================================== /posts/create
//======================================================================================================================

    //      Essentially we see a blank form when we load the page.
//      We are displaying the 'title' and 'description' properties of a new Post(), which doesn't
//      have any values set for these properties.
//      We have to make it have an empty object to fill.
//      So we need to first prepare the form to have a Post object to be submitted (and later, Category)
//      Therefore on the create.html we use a thymeleaf object <form th:object="${post}">, and the thymeleaf fields:
//      th:field:*{title}, th:field:*{description}. the * means post.title, post.description
    @GetMapping("/posts/create")
    public String showCreatePostForm(Model viewModel) {
        viewModel.addAttribute("post", new Post());
        viewModel.addAttribute("categories", categoriesDao.findAll());
        return "/posts/create";
    }

    //      Now in @PostMapping, Post will automatically have the title and description that was submitted with the form.
//      This is why it's good to have an empty constructor Post(){} to handle this.
//      Also set the User of the Post to the User who is logged in, and set the Date.
    @PostMapping("/posts/create")
    public String createPost(@Valid Post post,
                             Errors validation, Model viewModel) {
//      @Valid Post post now calls @ModelAttribute Post post first/instead and calls the validations!

        // Validation:
        if (validation.hasErrors()) {
            viewModel.addAttribute("errors", validation);
            viewModel.addAttribute("post", post);
            viewModel.addAttribute("categories", categoriesDao.findAll());
            return "/posts/create";
        }

//        XSSPrevent xp = new XSSPrevent();
//        xp.setAsText(post.getTitle());
//        post.setDescription(xp.getAsText());
//        This XSSPrevent isn't allowing me to update my code? What gives?

        post.setTitle(post.titleToUppercase(post.getTitle()));
        post.setUser(userSvc.loggedInUser());
        post.setDate(LocalDateTime.now());
        postSvc.save(post);
        return "redirect:/posts";
    }

//================================================= EDIT POST ========================================= /posts/{id}/edit
//======================================================================================================================

    @GetMapping("/posts/{id}/edit")
    public String showEditPostForm(@PathVariable Long id, Model viewModel) {
        Post existingPost = postSvc.findOne(id);
        User postOwner = existingPost.getUser();
        User loggedInUser = userSvc.loggedInUser();

        List<String> roles = rolesDao.ofUserWith(loggedInUser.getUsername());
        for(String role : roles) {
            if (role.equals("ROLE_ADMIN")) {
                viewModel.addAttribute("isLoggedInUserAdmin", role);
                viewModel.addAttribute("categories", categoriesDao.findAll());
                viewModel.addAttribute("post", existingPost);
                return "/posts/edit";
            }
        }

        if(!postOwner.getId().equals(loggedInUser.getId())) {
            return "redirect:/posts";
        } else {
            viewModel.addAttribute("categories", categoriesDao.findAll());
            viewModel.addAttribute("post", existingPost);
            return "/posts/edit";
        }
    }

    @PostMapping("/posts/{id}/edit")
    public String update(@PathVariable Long id, @Valid Post post, Errors validation, Model viewModel, RedirectAttributes redir) {
        // @ModelAttribute = expecting a post object to update/delete @ Valid takes care of this instead.
        // @Valid now calls @ModelAttribute first/instead and calls the validations!
        if (validation.hasErrors()) {
            viewModel.addAttribute("errors", validation);
            viewModel.addAttribute("categories", categoriesDao.findAll());
            viewModel.addAttribute("post", post);
            return "/posts/edit";
        }


        Post existingPost = postSvc.findOne(id);
        User postOwner = existingPost.getUser();
        User loggedInUser = userSvc.loggedInUser();

        List<String> roles = rolesDao.ofUserWith(loggedInUser.getUsername());
        for(String role : roles) {
            if(role.equals("ROLE_ADMIN") && (loggedInUser.getId().equals(postOwner.getId()))) {
                post.setUser(loggedInUser);
            } else {
                post.setUser(postOwner);
            }
        }

//        XSSPrevent xp = new XSSPrevent();
//        xp.setAsText(post.getTitle());
//        post.setDescription(xp.getAsText());

        post.setTitle(post.titleToUppercase(post.getTitle()));
        post.setId(id);
        postSvc.save(post);
        redir.addFlashAttribute("title", post.getTitle());
        return "redirect:/posts";
    }

//=================================================== DELETE POST ======================================================
//======================================================================================================================

    @PostMapping("/posts/{id}/delete")
    public String delete(@PathVariable long id, Post post, Comment comment) {

        Post existingPost = postSvc.findOne(id);
        User postOwner = existingPost.getUser();
        User loggedInUser = userSvc.loggedInUser();

        List<String> roles = rolesDao.ofUserWith(loggedInUser.getUsername());
        for(String role : roles) {
            if(role.equals("ROLE_ADMIN") && (!loggedInUser.getId().equals(postOwner.getId()))) {
                postSvc.delete(id);
            }
        }

        if(!loggedInUser.getId().equals(postOwner.getId())) {
            return "redirect:/posts";
        } else {
            // As I delete a post, comments and replies that belong to that post will be deleted too.
            // Set id's:
            comment.setPost(post);
            post.setId(id);
//      -- UPDATE: relationship cascades delete the List of comments that belong to the post id
//      commentSvc.delete(commentSvc.commentsOnPost(id));
            postSvc.delete(id);
            return "redirect:/profile";
        }
    }

//================================================ DISABLE AND ENABLE POSTS =============================================
//======================================================================================================================


    @PostMapping("/posts/{id}/disable")
    public String disablePost(@PathVariable long id) {
        Post post = postSvc.findOne(id);
        post.disable();
        postSvc.save(post);
        return "redirect:/posts";
    }

    @PostMapping("/posts/{id}/enable")
    public String enablePost(@PathVariable long id) {
        Post post = postSvc.findOne(id);
        post.enable();
        postSvc.save(post);
        return "redirect:/posts";
    }

//================================================= SEARCH POST ========================================================
//======================================================================================================================

    @GetMapping("/posts/search-tag/{term}")
    public String searchByTag(@PathVariable String term, Model viewModel) {
        viewModel.addAttribute("searchedContent", postSvc.searchPostsByKeyword(term));
        return "fragments/searchedPosts :: searchedPosts";
    }

    @GetMapping("/posts/search")
    public String search(@RequestParam String term, Model viewModel) {
        // return "list of posts where title is like ? or description is like ? or username is like ? etc etc"
        // Go to PostRepository and make a query method.
        // Go to PostService and implement it there too. Call it here and pass it to the view:
        viewModel.addAttribute("searchedContent", postSvc.searchPostsByKeyword(term));
        viewModel.addAttribute("categories", categoriesDao.findAll());
        viewModel.addAttribute("mostCommentedPosts", postSvc.popularPostsByCommentActivity());
        viewModel.addAttribute("mostLikedPosts", postSvc.popularPostsByLikes());
        viewModel.addAttribute("mostViewedPosts", postSvc.popularPostsByViews());
        viewModel.addAttribute("popularUsers", usersDao.popularUsersByKarma());
        viewModel.addAttribute("karmas", usersDao.popularUsersKarma());
        return "/posts/search";
    }

//============================================ MARKDOWN EDITOR PREVIEW =================================================
//======================================================================================================================

    @GetMapping("/posts/title.json")
    @ResponseBody
    public String showMarkdownPreviewInTitle(@RequestParam(name = "title") String title) {
        //@RequestParam name = "title" refers to the ajax data's property name, data: {title: ____ }
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(parser.parse(title));
    }

    @GetMapping("/posts/subtitle.json")
    @ResponseBody
    public String showMarkdownPreviewInSubtitle(@RequestParam(name = "subtitle") String subtitle) {
        //@RequestParam name = "image" refers to the ajax data's property name, data: {image: ____ }
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(parser.parse(subtitle));
    }

    @GetMapping("/posts/description.json")
    @ResponseBody
    public String showMarkdownPreviewInDescription(@RequestParam(name = "content") String content) {
        //@RequestParam name = "content" refers to the ajax data's property name, data: {content: ____ }
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(parser.parse(content));
    }

    @GetMapping("/posts/image.json")
    @ResponseBody
    public String showMarkdownPreviewInImage(@RequestParam(name = "image") String image) {
        //@RequestParam name = "image" refers to the ajax data's property name, data: {image: ____ }
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(parser.parse(image));
    }
}