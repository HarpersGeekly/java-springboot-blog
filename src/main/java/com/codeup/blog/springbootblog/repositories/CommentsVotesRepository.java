package com.codeup.blog.springbootblog.repositories;

import com.codeup.blog.springbootblog.Models.CommentVote;
import com.codeup.blog.springbootblog.Models.CommentVoteId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CommentsVotesRepository extends CrudRepository<CommentVote, CommentVoteId> {

    @Query(nativeQuery = true,
            value = "SELECT SUM(type) FROM comments_votes cv " +
                    "LEFT JOIN comments c ON c.id = cv.comment_id " +
                    "inner JOIN users u ON u.id = c.user_id " +
                    "WHERE u.id = ?1;")
    long totalPostKarmaForUser(Long id);
}
