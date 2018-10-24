package com.codeup.blog.springbootblog.repositories;

import com.codeup.blog.springbootblog.Models.CommentFlag;
import com.codeup.blog.springbootblog.Models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentsFlagsRepository extends CrudRepository<CommentFlag, Long> {

    CommentFlag findOneByCommentId(Long id);

//    @Query(nativeQuery = true, value = "SELECT count(comment_id) from comment_flags where comment_id LIKE ?1")
//    Long flagCountByCommentId(Long id);
    Long countCommentFlagByCommentId(Long id);
}
