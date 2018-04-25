package com.codeup.blog.springbootblog.repositories;

import com.codeup.blog.springbootblog.Models.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriesRepository extends CrudRepository<Category, Long>{
}
