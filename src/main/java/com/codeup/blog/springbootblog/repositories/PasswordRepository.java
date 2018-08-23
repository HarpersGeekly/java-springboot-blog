package com.codeup.blog.springbootblog.repositories;

import com.codeup.blog.springbootblog.Models.PasswordToken;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface PasswordRepository extends CrudRepository<PasswordToken, Long> {

    PasswordToken findByToken(String token);

    @Transactional
    void deleteById(Long id);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value="DELETE FROM reset_password WHERE user_id LIKE ?1")
    void deleteByUserId(Long id);
}
