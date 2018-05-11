package com.codeup.blog.springbootblog.repositories;

import com.codeup.blog.springbootblog.Models.PostVote;
import com.codeup.blog.springbootblog.Models.PostVoteId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostsVotesRepository extends CrudRepository<PostVote, PostVoteId>{

    //    There is no Boolean type in Oracle SQL. You will need to return a 1 or 0, or some such and act accordingly:
    @Query(nativeQuery = true,
            value="SELECT CASE WHEN COUNT(post_id) > 0 THEN 1 ELSE 0 END FROM posts_votes WHERE user_id =?1")
    int hasVoted(Long id);

    @Query(nativeQuery = true,
            value = "SELECT SUM(type) FROM posts_votes pv " +
                    "LEFT JOIN posts p ON p.id = pv.post_id " +
                    "inner JOIN users u ON u.id = p.user_id " +
                    "WHERE u.id = ?1;")
    long totalPostKarmaForUser(Long id);
}
