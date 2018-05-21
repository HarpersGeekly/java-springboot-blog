package com.codeup.blog.springbootblog.repositories;

import com.codeup.blog.springbootblog.Models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    @Query(nativeQuery = true,
            value = "SELECT " +
                    "(SELECT IFNULL(SUM(type), 0) FROM posts_votes pv LEFT JOIN posts p ON p.id = pv.post_id inner JOIN users u ON u.id = p.user_id  WHERE u.id LIKE ?1)" +
                    " + (SELECT IFNULL(SUM(type), 0) FROM comments_votes cv LEFT JOIN comments c ON c.id = cv.comment_id inner JOIN users u ON u.id = c.user_id WHERE u.id LIKE ?1)")
    long totalKarma(Long id);

//    @Query()
//    List<User> popularUsersByKarma;


//    @Query("select * from users u where u.email = ?1")
//  User findByEmailQuery(String email);

}
