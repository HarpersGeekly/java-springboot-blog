package com.codeup.blog.springbootblog.services;

import com.codeup.blog.springbootblog.Models.Comment;
import com.codeup.blog.springbootblog.Models.Post;
import com.codeup.blog.springbootblog.repositories.CommentsRepository;
import com.codeup.blog.springbootblog.repositories.CommentsVotesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service("commentService")
public class CommentService {

    private final CommentsRepository commentsDao;
    private final CommentsVotesRepository commentsVoteDao;
    private final UserService userSvc;

    @Autowired
    CommentService(CommentsRepository commentsDao, UserService userSvc, CommentsVotesRepository commentsVoteDao) {
        this.commentsDao = commentsDao;
        this.userSvc = userSvc;
        this.commentsVoteDao = commentsVoteDao;
    }

    private static final int MAX_COMMENT_LEVEL = 5;

    public Comment findOne(Long id) {
        return commentsDao.findOne(id);
    }

    public Comment save(Comment comment) {
        return commentsDao.save(comment);
    }

    public void delete(Long id) {
        commentsDao.delete(id);
    }

    public List<Comment> commentsOnPost(Long postId) {
        return commentsDao.commentsOnPost(postId);
    }

    public List<Comment> findAllByUserIdOrderByDateDesc(Long id) {
        return commentsDao.findAllByUserIdOrderByDateDesc(id);
    }

    public Comment saveNewComment(Post post, Comment parent, String body) {
//      if (parent.getId() != null) {
//            Comment parentComment = findOne(parent.getId());
//
//            int level = parentComment.commentLevel();
//
//            parent.setParentComment(level < MAX_COMMENT_LEVEL ? parentComment : parentComment.getParentComment());
//        }

        Comment comment = new Comment();
        comment.setBody(body);
        comment.setPost(post);
        comment.setParentComment(parent);
        comment.setDate(LocalDateTime.now());
        comment.setUser(userSvc.loggedInUser());
        commentsDao.save(comment);
        return comment;
    }


    public Page<Comment> postCommentsByPage(Long id, Pageable pageable) {
        return commentsDao.postCommentsByPage(id, pageable);
    }

    public long numberofCommentsOnPost(Long id) {
        return commentsDao.numberOfCommentsOnPost(id);
    }




}
