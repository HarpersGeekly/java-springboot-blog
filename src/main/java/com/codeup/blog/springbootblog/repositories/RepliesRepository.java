package com.codeup.blog.springbootblog.repositories;

import com.codeup.blog.springbootblog.Models.Reply;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by RyanHarper on 12/13/17.
 */
@Repository
public interface RepliesRepository extends CrudRepository<Reply, Long>{

    @Query(nativeQuery = true,
            value ="SELECT * FROM replies r WHERE r.comment_id = ?1")
    List<Reply> repliesToComments(long id);
}
