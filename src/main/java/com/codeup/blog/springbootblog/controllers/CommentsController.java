package com.codeup.blog.springbootblog.controllers;

import com.codeup.blog.springbootblog.Models.*;
import com.codeup.blog.springbootblog.repositories.CommentsFlagsRepository;
import com.codeup.blog.springbootblog.repositories.UsersRepository;
import com.codeup.blog.springbootblog.services.CommentService;
import com.codeup.blog.springbootblog.services.PostService;
import com.codeup.blog.springbootblog.services.PostVoteService;
import com.codeup.blog.springbootblog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class CommentsController {

    private PostService postSvc;
    private UserService userSvc;
    private CommentService commentSvc;
    private PostVoteService postVoteSvc;
    private FormatterUtil formatter;
    private UsersRepository usersDao;
    private CommentsFlagsRepository commentsFlagsDao;

    @Autowired
    public CommentsController(PostService postSvc, UserService userSvc, CommentService commentSvc, PostVoteService postVoteSvc, FormatterUtil formatter, UsersRepository usersDao, CommentsFlagsRepository commentsFlagsDao) {
        this.postSvc = postSvc;
        this.userSvc = userSvc;
        this.commentSvc = commentSvc;
        this.postVoteSvc = postVoteSvc;
        this.formatter = formatter;
        this.usersDao = usersDao;
        this.commentsFlagsDao = commentsFlagsDao;
    }

    // This annotation and method allows for the viewModel attribute "formatter" to persist across all handler methods, i.e. any page you visit within this controller gets to use "formatter" in the view.
    // The @ModelAttribute is an annotation that binds a method parameter or method return value to a named model attribute and then exposes it to a web view.
    @ModelAttribute("formatter")
    public FormatterUtil addFormatterToViews(Model viewModel) {
        viewModel.addAttribute("formatter", formatter);
        return formatter;
    }

//============================================= COMMENT ON A POST ======================================================
//======================================================================================================================

    @PostMapping("/posts/{postId}")
    public
//    public @ResponseBody no longer converting the comment into a json string. now returning a template
    String postComment(@PathVariable Long postId, @Valid Comment comment, BindingResult validation,
                       Model viewModel, Authentication token) {

        Post post = postSvc.findOne(postId);
        User postOwner = post.getUser();
        User user = usersDao.findById(userSvc.loggedInUser().getId());
//        System.out.println(token.getPrincipal().equals(userSvc.loggedInUser())); [true]

        System.out.println("get to comment on post controller:");
        viewModel.addAttribute("post", post);
        viewModel.addAttribute("postOwner", postOwner);
        viewModel.addAttribute("isPostOwner", userSvc.isLoggedInAndOwnerMatchesUser(post.getUser())); // show post edit button
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

        System.out.println("before save:" + comment.getBody());
        comment.setPost(post);
        comment.setUser(user);
        comment.setDate(LocalDateTime.now());
        commentSvc.save(comment);

        System.out.println("after save:" + comment.getBody());
//      return comment;
//      By returning this fragment (fragments/comments.html), we get all of our Thymeleaf-operated HTML
        return "fragments/commentsParents :: ajaxParent";
    }

//=============================================== EDIT A COMMENT  ==============/posts/{postId}/comment/{commentId}/edit
//======================================================================================================================

//    @GetMapping("/comment")
//    public @ResponseBody Comment test() {
//        return commentSvc.findOne(1225L);
//    }

    @GetMapping("/posts/comment/{commentId}/edit")
    public @ResponseBody
    Comment editComment(@PathVariable Long commentId, Model viewModel) {
        Comment comment = commentSvc.findOne(commentId);
        viewModel.addAttribute("comment", commentSvc.findOne(commentId));
        return comment;
    }

    @PostMapping("/posts/comment/{commentId}/edit")
    public @ResponseBody
    Comment submitEditedComment(@PathVariable Long commentId,
                                @RequestParam("body") String body, Model viewModel) {
        Comment comment = commentSvc.findOne(commentId);
        comment.setBody(body);
        commentSvc.save(comment);
        viewModel.addAttribute("comment", commentSvc.findOne(commentId));
        return comment;
    }

//=============================================== DELETE A COMMENT =====================================================
//======================================================================================================================

    @PostMapping("/posts/comment/{commentId}/delete")
    public @ResponseBody
    Comment deleteComment(@PathVariable Long commentId, Model viewModel) {
        Comment comment = commentSvc.findOne(commentId);

        commentSvc.delete(commentId);

//        comment.setBody("[deleted]");
//        boolean isDeleted = comment.delete();
//        viewModel.addAttribute("comment", comment);
//        viewModel.addAttribute("isDeleted", isDeleted);

//        commentSvc.save(comment);
        return comment;
    }

//Before ajax:
//    @PostMapping("/posts/{postId}/comment/{commentId}/delete")
//    public String deleteComment(@PathVariable Long postId, @PathVariable Long commentId) {
//        commentSvc.delete(commentId);
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

        Comment comment = commentSvc.findOne(commentId);
        User user = userSvc.loggedInUser();

        if (type.equalsIgnoreCase("upvote")) {
            comment.addVote(CommentVote.up(comment, user));
        } else {
            comment.addVote(CommentVote.down(comment, user));
        }

        commentSvc.save(comment);
        return comment;
    }

    @PostMapping("/comment/{commentId}/removeVote")
    public @ResponseBody
    Comment commentVoteRemoval(@PathVariable Long commentId) {

        Comment comment = commentSvc.findOne(commentId);
        User user = userSvc.loggedInUser();

        List<CommentVote> commentVotes = comment.getCommentVotes();

        for (CommentVote vote : commentVotes) {
            if (vote.voteBelongsTo(user)) {
                comment.removeVote(vote);
                break;
            }
        }
        commentSvc.save(comment);
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
        User postOwner = post.getUser();
        User loggedInUser = userSvc.loggedInUser();
        Comment parent = commentSvc.findOne(parentId);

        if (validation.hasErrors()) {
            viewModel.addAttribute("comment", comment);
            return "fragments/commentError :: ajaxError";
        }

        Comment newComment = commentSvc.saveNewComment(post, parent, body); // saving in the comment service
        viewModel.addAttribute("comment", newComment);
        viewModel.addAttribute("post", post);
        viewModel.addAttribute("postOwner", postOwner);
        viewModel.addAttribute("isPostOwner", userSvc.isLoggedInAndOwnerMatchesUser(post.getUser()));
        viewModel.addAttribute("isLoggedIn", userSvc.isLoggedIn());

        if (loggedInUser != null) {
            long loggedInUserId = loggedInUser.getId();
            User user = usersDao.findById(loggedInUserId);
            boolean isBanned = user.isBanned();
            viewModel.addAttribute("isBanned", isBanned);
        }

//        viewModel.addAttribute("comments", commentSvc.commentsOnPost(post.getId()));
        return "fragments/comments :: ajaxComment";
    }

    @GetMapping("/posts/retrieveUsername/comment/{commentId}")
    public @ResponseBody
    User retrieveUsernameForReplyTextarea(@PathVariable Long commentId) {
        Comment comment = commentSvc.findOne(commentId);
        return comment.getUser();
    }

    //=================================== FLAG COMMENT ======================================

    @PostMapping("/comment/{id}/flag")
    public @ResponseBody
    CommentFlag makeFlag(@PathVariable Long id) {

        Comment comment = commentSvc.findOne(id);
        User loggedInUser = userSvc.loggedInUser();
        User user = usersDao.findById(loggedInUser.getId());

        //check if a user has already flagged this comment (can't flag again)
        boolean hasReported = comment.commentHasBeenFlaggedByLoggedInUser(user);
        if(!hasReported) {
            CommentFlag commentFlag = new CommentFlag();
            commentFlag.setComment(comment);
            commentFlag.setFlagger(user);
            commentsFlagsDao.save(commentFlag);

            if(comment.getCommentFlags() != null && comment.getCommentFlags().size() > 1) {
                System.out.println("comment is majorly flagged");
                //send a notification to all admins??? or have the admin dashboard only show comments that are flagged
            }

            return commentFlag;
        } else {
            return null;
        }
    }



}
