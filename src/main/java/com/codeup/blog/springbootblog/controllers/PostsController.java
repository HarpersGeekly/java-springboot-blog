package com.codeup.blog.springbootblog.controllers;

import com.codeup.blog.springbootblog.Models.*;
import com.codeup.blog.springbootblog.repositories.CategoriesRepository;
import com.codeup.blog.springbootblog.repositories.CommentsRepository;
import com.codeup.blog.springbootblog.repositories.UsersRepository;
import com.codeup.blog.springbootblog.services.CommentService;
import com.codeup.blog.springbootblog.services.PostService;
import com.codeup.blog.springbootblog.services.PostVoteService;
import com.codeup.blog.springbootblog.services.UserService;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
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

    private final PostVoteService postVoteSvc;

    private final UsersRepository usersDao; //making queries to database

    private final UserService userSvc; //

    private final CommentService commentSvc;
    private static final int MAX_COMMENT_LEVEL = 5;

    private final CommentsRepository commentsDao;

    private final CategoriesRepository categoriesDao;

    // Constructor "dependency injection", passing the PostService object into the PostController constructor,
    // everything ties together now. Services + Controller.
    // Autowiring makes it so we don't have to build the object ourselves in the main method of SpringBlogApplication,
    // Spring handles this.

    // Before I made a PostsRepository, I used dummy hard code in the PostService
    // in the form of a List<Post> posts. But now the PostsRepository is being built in the PostsService
    // Reminder: There are built in methods for CRUDRepository:
    // findAll(), findOne(), save(), delete(). These are in the Service

    public PostsController(PostService postSvc,
                           PostVoteService postVoteSvc,
                           UsersRepository usersDao,
                           UserService userSvc,
                           CommentService commentSvc,
                           CommentsRepository commentsDao,
                           CategoriesRepository categoriesDao
    ) {
        this.postSvc = postSvc;
        this.postVoteSvc = postVoteSvc;
        this.usersDao = usersDao;
        this.userSvc = userSvc;
        this.commentSvc = commentSvc;
        this.commentsDao = commentsDao;
        this.categoriesDao = categoriesDao;
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

        viewModel.addAttribute("posts", postSvc.postsByResultSetIndexPage());
        viewModel.addAttribute("categories", categoriesDao.findAll());
        viewModel.addAttribute("mostCommentedPosts", postSvc.popularPostsByCommentActivity());
        viewModel.addAttribute("mostLikedPosts", postSvc.popularPostsByLikes());
        viewModel.addAttribute("popularUsers", usersDao.popularUsersByKarma());
        viewModel.addAttribute("karmas", usersDao.popularUsersKarma());
        return "posts/index";
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
        return "posts/create";
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

//        List<Category> categories = existingPost.getCategories();
        viewModel.addAttribute("categories", categoriesDao.findAll());
        viewModel.addAttribute("post", existingPost);
        return "/posts/edit";
    }

    @PostMapping("/posts/{id}/edit")
    public String update(@PathVariable Long id, @Valid Post post, Errors validation, Model viewModel) {
        // @ModelAttribute = expecting a post object to update/delete @ Valid takes care of this instead.
        // @Valid now calls @ModelAttribute first/instead and calls the validations!
        if (validation.hasErrors()) {
            viewModel.addAttribute("errors", validation);
            viewModel.addAttribute("categories", categoriesDao.findAll());
            viewModel.addAttribute("post", post);
            return "/posts/edit";
        }

//        XSSPrevent xp = new XSSPrevent();
//        xp.setAsText(post.getTitle());
//        post.setDescription(xp.getAsText());

        post.setUser(userSvc.loggedInUser());
        post.setId(id);
        postSvc.save(post);
        return "redirect:/posts/{id}";
    }

//=================================================== DELETE POST ======================================================
//======================================================================================================================

    @PostMapping("/posts/{id}/delete")
    public String delete(@PathVariable long id, Post post, Comment comment) {

//      As I delete a post, comments and replies that belong to that post will be deleted too.

        // Set id's:
        comment.setPost(post);
        post.setId(id);

//      Delete the List of comments that belong to the post id -- UPDATE: relationship cascades manage this.
//      commentSvc.delete(commentSvc.commentsOnPost(id));
        postSvc.delete(id);
        return "redirect:/profile";
    }

//================================================= SEARCH POST ========================================================
//======================================================================================================================

    @GetMapping("/posts/search")
    public String search(@RequestParam String term, Model viewModel) {
        // return "list of posts where title is like ? or description is like ? or username is like ? etc etc"
        // Go to PostRepository and make a query method.
        // Go to PostService and implement it there too. Call it here and pass it to the view:
        viewModel.addAttribute("searchedContent", postSvc.searchPostsByKeyword(term));
        viewModel.addAttribute("categories", categoriesDao.findAll());
        viewModel.addAttribute("mostCommentedPosts", postSvc.popularPostsByCommentActivity());
        viewModel.addAttribute("mostLikedPosts", postSvc.popularPostsByLikes());
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

    @GetMapping("/posts/description.json")
    @ResponseBody
    public String showMarkdownPreviewInDescription(@RequestParam(name = "content") String content) {
        //@RequestParam name = "content" refers to the ajax data's property name, data: {content: ____ }
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(parser.parse(content));
    }


//============================================ MARKDOWN EDITOR IMAGE UPLOAD =================================================
//======================================================================================================================

    @GetMapping("/posts/image.json")
    @ResponseBody
    public String showMarkdownImageInForm() {
        return "";
    }

//================================================= SHOW POST ============================================== /posts/{id}
//======================================================================================================================

    @GetMapping("/posts/{id}")
    public String showPostById(@PathVariable Long id, Model viewModel, Comment comment) {
//                               @PageableDefault(value = 11, sort = "id", direction = Sort.Direction.DESC)
//                                       Pageable pageable) {
//        Post post = new Post(1L,"First Title", "First Description");
        Post post = postSvc.findOne(id);
        User user = userSvc.loggedInUser();

        System.out.println("get to show.html controller");

        viewModel.addAttribute("post", post);
        viewModel.addAttribute("isLoggedIn", userSvc.isLoggedIn());
        viewModel.addAttribute("isPostOwner", userSvc.isLoggedInAndPostMatchesUser(post.getUser())); // show post edit button
        viewModel.addAttribute("comment", comment);
        viewModel.addAttribute("comments", commentSvc.commentsOnPost(id));
        viewModel.addAttribute("isParentComment", comment.isParentComment(comment));
//        viewModel.addAttribute("comment", new Comment());
//        viewModel.addAttribute("page", commentSvc.postCommentsByPage(id, pageable));
        viewModel.addAttribute("categories", categoriesDao.findAll());

        if (user != null) {
            PostVote vote = post.getVoteFrom(user);
            viewModel.addAttribute("upvote", vote != null && vote.isUpvote());
            viewModel.addAttribute("downvote", vote != null && vote.isDownVote());
        }

        return "posts/show";
    }

//============================================= COMMENT ON A POST ======================================================
//======================================================================================================================

    @PostMapping("/posts/{postId}")
    public
//    public @ResponseBody no longer converting the comment into a json string. now returning a template
    String postComment(@PathVariable Long postId, @Valid Comment comment, BindingResult validation, Model viewModel) {

        Post post = postSvc.findOne(postId);
        viewModel.addAttribute("post", post);
        viewModel.addAttribute("isPostOwner", userSvc.isLoggedInAndPostMatchesUser(post.getUser())); // show post edit button
        viewModel.addAttribute("isLoggedIn", userSvc.isLoggedIn());
        viewModel.addAttribute("comment", comment);

        if (validation.hasErrors()) {
//            viewModel.addAttribute("errors", validation); // By using BindingResult validation instead of Error validation, don't need "errors" attritbute
            viewModel.addAttribute("comment", comment);
//            validation.rejectValue(
//                    "body",
//                    "comment.body",
//                    "Comments must be at least 2 characters.");

            // return a fragment with only the errors and not:
//            return "/posts/show"; //html page.
            return "fragments/commentError :: ajaxError";
        }

        comment.setPost(post);
        comment.setUser(userSvc.loggedInUser());
        comment.setDate(LocalDateTime.now());
        commentSvc.save(comment);

//      return comment;
//      By returning this fragment (fragments/comments.html), we get all of our Thymeleaf-operated HTML
        return "fragments/comments :: ajaxComment";
    }

//=============================================== EDIT A COMMENT  ==============/posts/{postId}/comment/{commentId}/edit
//======================================================================================================================

//    @GetMapping("/comment")
//    public @ResponseBody Comment test() {
//        return commentSvc.findOne(1225L);
//    }

    @GetMapping("/posts/{postId}/comment/{commentId}/edit")
    public @ResponseBody
    Comment editComment(@PathVariable Long postId,
                        @PathVariable Long commentId, Model viewModel) {
        Comment comment = commentsDao.findOne(commentId);
        viewModel.addAttribute("comment", commentsDao.findOne(commentId));
        return comment;
    }

    @PostMapping("/posts/{postId}/comment/{commentId}/edit")
    public @ResponseBody
    Comment submitEditedComment(@PathVariable Long postId,
                                @PathVariable Long commentId,
                                @RequestParam("body") String body, Model viewModel) {
        Comment comment = commentsDao.findOne(commentId);
        comment.setBody(body);
        commentsDao.save(comment);
        viewModel.addAttribute("comment", commentsDao.findOne(commentId));
        return comment;
    }

//=============================================== DELETE A COMMENT =====================================================
//======================================================================================================================

    @PostMapping("/posts/{postId}/comment/{commentId}/delete")
    public @ResponseBody
    Comment deleteComment(@PathVariable Long postId, @PathVariable Long commentId) {
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

//    @GetMapping("/test")
//    public @ResponseBody Post test() {
//        return postSvc.findOne(33L);
//    }

//================================================= POST VOTING ========================================================
//======================================================================================================================

    @PostMapping("/posts/{type}/{postId}")
    public @ResponseBody
    Post postVoting(@PathVariable Long postId, @PathVariable String type,
                    Authentication token) {

        Post post = postSvc.findOne(postId);
        User user = (User) token.getPrincipal(); //userSvc.loggedInUser()));

        if (type.equalsIgnoreCase("upvote")) {
            post.addVote(PostVote.up(post, user));
        } else {
            post.addVote(PostVote.down(post, user));
        }

        postSvc.save(post);
        return post;
//        return"posts/show";
    }

    @PostMapping("/posts/{postId}/removeVote")
    public @ResponseBody
    Post voteRemoval(@PathVariable Long postId) {

        Post post = postSvc.findOne(postId);
        User user = userSvc.loggedInUser();

        List<PostVote> votes = post.getVotes();

//        Excuse to mess around with foreach:
//        votes.forEach(vote -> System.out.println(vote));

        System.out.println("vote count:" + post.voteCount());

        for (PostVote vote : votes) {
//            if (vote.getUser().getId() == (user.getId())) {
            if (vote.voteBelongsTo(user)) {
                post.removeVote(vote);
                postVoteSvc.delete(vote);
                postSvc.save(post);
                System.out.println("vote count:" + post.voteCount());
                break;
            }
        }
        postSvc.save(post);
        return post;
    }

//================================================ COMMENT VOTING ======================================================
//======================================================================================================================

    @PostMapping("/comment/{type}/{commentId}")
    public @ResponseBody
    Comment commentVoting(@PathVariable String type,
                          @PathVariable Long commentId) {

        Comment comment = commentsDao.findOne(commentId);
        User user = userSvc.loggedInUser();

        if (type.equalsIgnoreCase("upvote")) {
            comment.addVote(CommentVote.up(comment, user));
        } else {
            comment.addVote(CommentVote.down(comment, user));
        }

        commentsDao.save(comment);
        return comment;
    }

    @PostMapping("/comment/{commentId}/removeVote")
    public @ResponseBody
    Comment commentVoteRemoval(@PathVariable Long commentId) {

        Comment comment = commentsDao.findOne(commentId);
        User user = userSvc.loggedInUser();

        List<CommentVote> commentVotes = comment.getCommentVotes();

        for (CommentVote vote : commentVotes) {
            if (vote.voteBelongsTo(user)) {
                comment.removeVote(vote);
                break;
            }
        }
        commentsDao.save(comment);
        return comment;
    }

//=============================================== COMMENT REPLIES ======================================================
//======================================================================================================================

    @PostMapping("/posts/{postId}/comment/{parentId}/reply")
    public String reply(@PathVariable Long postId, @PathVariable Long parentId, @RequestParam("body") String body, @Valid Comment comment,
                        BindingResult validation,
                        Model viewModel) {
        System.out.println("Get to reply controller");

        Post post = postSvc.findOne(postId);
        Comment parent = commentSvc.findOne(parentId);
//        List<Comment> children = parent.getChildrenComments();
        viewModel.addAttribute("replyToUserId", parent.getUser().getId());
        viewModel.addAttribute("replyToUserUsername", parent.getUser().getUsername());

        if (validation.hasErrors()) {
            viewModel.addAttribute("comment", comment);
            return "fragments/commentError :: ajaxError";
        }

        Comment newComment = commentSvc.saveNewComment(post, parent, body); // saving in the comment service
        viewModel.addAttribute("comment", newComment);
//        viewModel.addAttribute("children", children);
        viewModel.addAttribute("post", post);
        viewModel.addAttribute("isPostOwner", userSvc.isLoggedInAndPostMatchesUser(post.getUser()));
        viewModel.addAttribute("isLoggedIn", userSvc.isLoggedIn());

//        viewModel.addAttribute("comments", commentsDao.commentsOnPost(post.getId()));
        return "fragments/comments :: ajaxComment";
    }

    @GetMapping("/posts/retrieveUsername/comment/{commentId}")
    public @ResponseBody
    User retrieveUsernameForReplyTextarea(@PathVariable Long commentId) {
        Comment comment = commentSvc.findOne(commentId);
        return comment.getUser();
    }

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
}




//        viewModel.addAttribute("nextResultSet", postSvc.postsByPage(pageable));
