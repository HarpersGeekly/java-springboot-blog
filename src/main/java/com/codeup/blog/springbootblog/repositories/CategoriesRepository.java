package com.codeup.blog.springbootblog.repositories;

import com.codeup.blog.springbootblog.Models.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriesRepository extends CrudRepository<Category, Long>{

    @Query(nativeQuery = true,
            value = "SELECT * from categories WHERE")
    List<Category> findAllByUserActivity(Long id);
}
