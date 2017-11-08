package com.codeup.blog.springbootblog.repositories;

import com.codeup.blog.springbootblog.Models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by RyanHarper on 11/7/17.
 */
@Repository
public interface UsersRepository extends CrudRepository<User, Long> {

    public User findByUsername(String username);

    public User findById(Long id);

    public User findByEmail(String password);

//    @Query("select u from users u where u.email = ?1")
    public User findByEmailQuery(String email);
}
