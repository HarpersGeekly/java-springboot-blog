package com.codeup.blog.springbootblog.repositories;

import com.codeup.blog.springbootblog.Models.Comment;
import com.codeup.blog.springbootblog.Models.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by RyanHarper on 11/20/17.
 */
public interface CommentsRepository extends CrudRepository<Comment, Long> {

//    @Query("SELECT * FROM comments c WHERE c.post_id = ?1")

//    public List<Comment> findAllCommentsWherePostIdIsLike(Long id);
}
