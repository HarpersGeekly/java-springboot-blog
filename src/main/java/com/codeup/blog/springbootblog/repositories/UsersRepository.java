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

    // select * from users where username = ? (thanks Spring!)
    User findByUsername(String username);

    // select * from users where id = ?
    User findById(Long id);

    // select * from users where email = ?
    User findByEmail(String password);

//    @Query("select * from users u where u.email = ?1")
//  User findByEmailQuery(String email);

}
