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
    @Query("SELECT DISTINCT p FROM Post p JOIN p.user u JOIN p.categories c WHERE p.title LIKE ?1 OR p.description LIKE ?1 OR u.username LIKE ?1 OR c.name LIKE ?1 ORDER BY p.id DESC")
    List<Post> searchPostsWithKeyword(String term);

    // When you land on the index page, you will always see the latest number of posts.
    @Query(nativeQuery = true, value="SELECT * from posts p ORDER BY p.id DESC LIMIT 4")
    List<Post> postsByResultSetIndexPage();

    // When you click the Load More Posts button, you will see the next set. Use this or pagination @Deprecated?
    @Query(nativeQuery = true, value="SELECT * from posts p ORDER BY p.id DESC") //LIMIT ?1
    List<Post> postsByResultSet();

    @Query(nativeQuery = true,
            value = "SELECT p.id, p.created_date, p.description, p.title, p.subtitle, p.header_image, p.user_id from posts p JOIN comments c ON p.id = c.post_id GROUP BY p.id ORDER BY count(*) DESC LIMIT 5")
    List<Post> popularPostsByCommentActivity();

    @Query(nativeQuery = true,
            value = "SELECT SUM(type), p.id, p.created_date, p.description, p.header_image, p.title, p.subtitle, p.user_id from posts_votes pv LEFT JOIN posts p ON p.id = pv.post_id GROUP BY p.id ORDER BY count(*) DESC LIMIT 5")
    List<Post> popularPostsByLikes();

    @Query(nativeQuery = true,
    value = "SELECT * FROM posts p WHERE user_id LIKE ?1 ORDER BY p.id DESC LIMIT 5")
    List<Post> postsByUserLimited(Long id);

    @Query(nativeQuery = true,
    value = "SELECT * FROM posts p WHERE user_id LIKE ?1 ORDER BY p.id DESC")
    List<Post> postsByUser(Long id);

    @Deprecated
    @Query(nativeQuery = true,
            countQuery = "SELECT count(*) FROM redwood_blog_db.posts p", /*need to count rows for pagination */
            value =
                    "SELECT * from redwood_blog_db.posts p ORDER BY p.id DESC, ?#{#pageable}")
    Page<Post> postsByPage(Pageable pageable);

}
