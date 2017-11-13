package com.codeup.blog.springbootblog.repositories;

import com.codeup.blog.springbootblog.Models.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by RyanHarper on 11/7/17.
 */
public interface PostsRepository extends CrudRepository<Post, Long> { // <Model, Primary Key>
    // There are built in methods for CRUDRepository, findAll(), findOne(), save(), delete().
    // Is not SQL query...it's HQL (hibernate) talking to the database not by table relationships but by object relationships
    @Query("SELECT p FROM Post p JOIN p.user u WHERE p.title LIKE ?1 OR p.description LIKE ?1 OR u.username LIKE ?1")
    List<Post> searchPostsWithKeyword(String term);
}
