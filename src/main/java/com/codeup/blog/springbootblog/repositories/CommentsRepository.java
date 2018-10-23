package com.codeup.blog.springbootblog.repositories;

import com.codeup.blog.springbootblog.Models.Comment;
//import com.codeup.blog.springbootblog.Models.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
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

//    @Query(nativeQuery = true, value="SELECT * from comments c where c.user_id=?1 ORDER BY c.created_date DESC")
    List<Comment> findAllByUserIdOrderByDateDesc(Long id);

//    @Query(nativeQuery = true,
////            ORDER BY ID/ NEWEST COMMENT...
////            value = "SELECT * FROM comments c WHERE c.post_id = ?1 AND c.parent_id IS NULL ORDER BY c.created_date ASC")
////            ORDER BY COMMENT COUNT?:
//            value = "SELECT c.id, c.body, c.created_date, c.post_id, c.user_id, c.parent_id, IFNULL(SUM(cv.type), 0) AS totalCommentVotes FROM comments c LEFT JOIN comments_votes cv ON cv.comment_id = c.id JOIN posts p ON c.post_id = p.id WHERE c.parent_id IS NULL AND c.post_id = ?1 GROUP BY c.id ORDER BY totalCommentVotes DESC")
//    List<Comment> commentsOnPost(Long id);
    List<Comment> findCommentsByPostId(Long id);

    @Query("SELECT c.id, c.body, c.user_id, c.created_date FROM comments c LEFT JOIN comment_flags cf ON c.id = cf.comment_id GROUP BY c.id HAVING COUNT(cf.comment_id) >= 10")
    List<Comment> mostFlaggedComments();

    @Deprecated
    @Query(nativeQuery = true,
            countQuery = "SELECT count(*) FROM redwood_blog_db.comments c WHERE c.post_id = ?1 AND c.parent_id IS null", /*need to count rows for pagination */
            value =
                    "SELECT * FROM redwood_blog_db.comments c WHERE c.post_id = ?1 AND c.parent_id IS null ORDER BY ?#{#pageable}")
    Page<Comment> postCommentsByPage(Long id, Pageable pageable);

    @Deprecated
    @Query(nativeQuery = true,
            value="SELECT count(*) FROM redwood_blog_db.comments c WHERE c.post_id = ?1")
    long numberOfCommentsOnPost(Long id);

//    @Query(nativeQuery = true,
//            value = "SELECT * FROM redwood_blog_db.comments c WHERE c.parent_id = ?1")
//    void deleteChildren(Long id);
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

