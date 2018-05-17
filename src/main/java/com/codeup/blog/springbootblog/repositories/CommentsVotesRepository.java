package com.codeup.blog.springbootblog.repositories;

import com.codeup.blog.springbootblog.Models.CommentVote;
import com.codeup.blog.springbootblog.Models.CommentVoteId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CommentsVotesRepository extends CrudRepository<CommentVote, CommentVoteId> {

}
