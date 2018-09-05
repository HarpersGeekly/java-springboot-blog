package com.codeup.blog.springbootblog.repositories;

import com.codeup.blog.springbootblog.Models.UserRole;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolesRepository extends CrudRepository<UserRole, Long> {

        @Query("select ur.role from UserRole ur, User u where u.username=?1 and ur.userId = u.id")
        List<String> ofUserWith(String username);

        @Query(nativeQuery = true, value="select ur.id from user_roles ur JOIN users u ON ur.user_id = u.id WHERE u.id=?1")
        long findByUserId(Long id);
}
