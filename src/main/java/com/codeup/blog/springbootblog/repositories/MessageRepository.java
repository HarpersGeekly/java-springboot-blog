package com.codeup.blog.springbootblog.repositories;

import com.codeup.blog.springbootblog.Models.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends CrudRepository<Message, Long> {
}
