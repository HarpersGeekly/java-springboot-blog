package com.codeup.blog.springbootblog.repositories;

import com.codeup.blog.springbootblog.Models.Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.ui.Model;

/**
 * Created by RyanHarper on 11/7/17.
 */
public interface PostsRepository extends CrudRepository<Post, Long> { // <Model, Primary Key>
    // There are built in methods for CRUDRepository, findAll(), findOne(), save(), delete().
}
