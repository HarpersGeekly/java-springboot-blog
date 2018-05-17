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
    @Query("SELECT p FROM Post p JOIN p.user u JOIN p.categories c WHERE p.title LIKE ?1 OR p.description LIKE ?1 OR u.username LIKE ?1 OR c.name LIKE ?1")
    List<Post> searchPostsWithKeyword(String term);

    @Query(nativeQuery = true,
            countQuery = "SELECT count(*) FROM redwood_blog_db.posts p", /*need to count rows for pagination */
            value =
                    "SELECT * from redwood_blog_db.posts p ORDER BY p.id DESC, ?#{#pageable}")
    Page<Post> postsByPage(Pageable pageable);

    @Query(nativeQuery = true,
            value = "SELECT p.id from posts p JOIN comments c ON p.id = c.post_id GROUP BY p.id ORDER BY count(*) DESC LIMIT 5")
    List<Post> popularPostsByCommentActivity();

    @Query(nativeQuery = true,
            value = "SELECT SUM(type) from posts_votes pv LEFT JOIN posts p ON p.id = pv.post_id GROUP BY p.id ORDER BY count(*) DESC LIMIT 5")
    List<Post> popularPostsByLikes();
}

//
//    SELECT c.id, c.city
//        FROM cities c
//        JOIN ( SELECT city, COUNT(*) AS cnt
//        FROM cities
//        GROUP BY city
//        ) c2 ON ( c2.city = c.city )
//        ORDER BY c2.cnt DESC;