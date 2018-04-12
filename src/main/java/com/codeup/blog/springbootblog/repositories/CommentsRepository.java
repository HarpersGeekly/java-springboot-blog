package com.codeup.blog.springbootblog.repositories;

import com.codeup.blog.springbootblog.Models.Comment;
//import com.codeup.blog.springbootblog.Models.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

/**
 * Created by RyanHarper on 11/20/17.
 */
@Repository
public interface CommentsRepository extends CrudRepository<Comment, Long> {

    //========================================================================================
//    @Query("SELECT * FROM comments c WHERE c.post_id = ?1")
    //ISSUE:
    // comments are being injected behind the last comment the user made, not the last comment A user made.
    // SOLUTION:
    // select all comments from a post id and order by comment id.
    // @Query uses Hibernate "HQL" object relationships not database relationships
    // nativeQuery = true makes it SQL compatible
    @Query(nativeQuery = true,
            countQuery = "SELECT count(*) FROM redwood_blog_db.comments c WHERE c.post_id = ?1", /*need to count rows for pagination */
            value =
                    "SELECT * FROM redwood_blog_db.comments c WHERE c.post_id = ?1 ORDER BY ?#{#pageable}")
    Page<Comment> postCommentsByPage(Long id, Pageable pageable);

//    @Query(nativeQuery = true,
//            value = "SELECT c.vote_count FROM redwood_blog_db.comments c WHERE c.id = ?1")
//    Long commentVoteCount(Long id);

    @Query(nativeQuery = true,
            value = "SELECT * FROM comments c WHERE c.post_id = ?1 ORDER BY c.id DESC")
    List<Comment> commentsOnPost(Long id);

    @Query(nativeQuery = true,
            value="SELECT count(*) FROM redwood_blog_db.comments c WHERE c.post_id = ?1")
    int numberOfCommentsOnPost(Long id);

    //=================================== BEFORE PAGINATION =====================================================
//    List<Comment> sortAllByTime(Long id);
//    "Time" only means the comment id in descending order...

//    ==== PAGINATION ====
//    Page<Comment> findAll(Pageable pageable); ???
//    Find all and sort???
//    Find all from comments where post_id =? order by comments id descending
//    public List<Comment> findAllCommentsWherePostIdIsLike(Long id);
//    ==== ERROR ====:
//InvalidJpaQueryMethodException:
//        Cannot use native queries with dynamic sorting and/or pagination in method public abstract org.springframework.data.domain.Page
//        com.codeup.blog.springbootblog.repositories.CommentsRepository.findAllByPostIdLikeOrderByCommentId(java.lang.Long,org.springframework.data.domain.Pageable)
//    ==== SOLUTION ===
//    Documentation -> StackOverflow -> "ORDER BY ?#{#pageable}"
}

