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
    User findByEmail(String email);

    @Query(nativeQuery = true,
            value = "SELECT " +
                    "(SELECT IFNULL(SUM(type), 0) FROM posts_votes pv LEFT JOIN posts p ON p.id = pv.post_id inner JOIN users u ON u.id = p.user_id  WHERE u.id LIKE ?1)" +
                    " + (SELECT IFNULL(SUM(type), 0) FROM comments_votes cv LEFT JOIN comments c ON c.id = cv.comment_id inner JOIN users u ON u.id = c.user_id WHERE u.id LIKE ?1)")
    long totalKarmaByUser(Long id);

    // select all from users where totalKarma ORDER BY DESC LIMIT 3
    @Query(nativeQuery = true, value = "SELECT u.id, u.username, pv_sum.total AS postTotal, cv_sum.total AS commentTotal, IFNULL(pv_sum.total, 0) + IFNULL(cv_sum.total, 0) as totalVotes FROM users u " +
            "LEFT JOIN (SELECT p.user_id, IFNULL(SUM(pv.type), 0) AS total FROM posts p JOIN posts_votes pv ON pv.post_id = p.id GROUP BY p.user_id) pv_sum ON pv_sum.user_id = u.id " +
            "LEFT JOIN (SELECT c.user_id, IFNULL(SUM(cv.type), 0) AS total FROM comments c JOIN comments_votes cv ON cv.comment_id = c.id GROUP BY c.user_id) cv_sum ON cv_sum.user_id = u.id " +
            "GROUP BY u.id, u.username, postTotal, commentTotal ORDER BY totalVotes DESC LIMIT 3")
    List<User> popularUsersByKarma();


    @Query(nativeQuery = true, value = "SELECT IFNULL(pv_sum.total, 0) + IFNULL(cv_sum.total, 0) as totalVotes FROM users u " +
            "LEFT JOIN (SELECT p.user_id, IFNULL(SUM(pv.type), 0) AS total FROM posts p JOIN posts_votes pv ON pv.post_id = p.id GROUP BY p.user_id) pv_sum ON pv_sum.user_id = u.id " +
            "LEFT JOIN (SELECT c.user_id, IFNULL(SUM(cv.type), 0) AS total FROM comments c JOIN comments_votes cv ON cv.comment_id = c.id GROUP BY c.user_id) cv_sum ON cv_sum.user_id = u.id " +
            "ORDER BY totalVotes DESC LIMIT 3")
    List<Long> popularUsersKarma();
//    @Query("select * from users u where u.email = ?1")
//  User findByEmailQuery(String email);

}
