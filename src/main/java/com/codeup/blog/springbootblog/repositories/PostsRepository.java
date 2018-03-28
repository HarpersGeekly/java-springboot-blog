package com.codeup.blog.springbootblog.repositories;


import com.codeup.blog.springbootblog.Models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by RyanHarper on 11/7/17.
 */
@Repository
public interface PostsRepository extends CrudRepository<Post, Long> { // <Model, Primary Key>
    // There are built in methods for CRUDRepository, findAll(), findOne(), save(), delete().
    // Is not SQL query...it's HQL (hibernate) talking to the database not by table relationships but by object relationships
    @Query("SELECT p FROM Post p JOIN p.user u WHERE p.title LIKE ?1 OR p.description LIKE ?1 OR u.username LIKE ?1")
    List<Post> searchPostsWithKeyword(String term);

    @Query(nativeQuery = true,
            countQuery = "SELECT count(*) FROM redwood_blog_db.posts", /*need to count rows for pagination */
            value =
                    "SELECT * FROM redwood_blog_db.posts ORDER BY ?#{#pageable} DESC")
    Page<Post> postsByPage(Pageable pageable);

    @Query(nativeQuery = true,
            value="SELECT vote_count FROM posts p WHERE p.id LIKE ?1")
    Long postVoteCount(Long id);
}
