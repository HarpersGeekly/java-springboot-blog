package com.codeup.blog.springbootblog.repositories;

import com.codeup.blog.springbootblog.Models.PasswordToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface PasswordRepository extends CrudRepository<PasswordToken, Long> {

    PasswordToken findByToken(String token);

    @Transactional
    void deleteById(Long id);
}
