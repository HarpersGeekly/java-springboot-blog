package com.codeup.blog.springbootblog.repositories;

import com.codeup.blog.springbootblog.Models.CommentFlag;
import com.codeup.blog.springbootblog.Models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentsFlagsRepository extends CrudRepository<CommentFlag, Long> {

    CommentFlag findOneByCommentId(Long id);
}
