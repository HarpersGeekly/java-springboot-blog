package com.codeup.blog.springbootblog.repositories;

import com.codeup.blog.springbootblog.Models.Comment;
import com.sun.xml.internal.bind.v2.TODO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

/**
 * Created by RyanHarper on 11/20/17.
 */
@Repository
public interface CommentsRepository extends CrudRepository<Comment, Long> {

// TODO: need to combine methods? findAll comments and sort them.

    Page<Comment> findAll(Pageable pageable);
//    @Query("SELECT * FROM comments c WHERE c.post_id = ?1")

//    public List<Comment> findAllCommentsWherePostIdIsLike(Long id);

    //ISSUE:
    // comments are being injected behind the last comment the user made, not the last comment A user made.
    // SOLUTION:
    // select all comments from a post id and order by comment id.
    // @Query uses Hibernate "HQL" object relationships not database relationships
    // nativeQuery = true makes it SQL compatible
    @Query(nativeQuery = true,
            value =
                    "SELECT * FROM redwood_blog_db.comments c WHERE c.post_id = ?1 ORDER BY c.id DESC")
    List<Comment> sortAllByTime(Long id);



}
