package com.codeup.blog.springbootblog.services;

import com.codeup.blog.springbootblog.Models.PostVote;
import com.codeup.blog.springbootblog.repositories.PostsVotesRepository;
import org.springframework.stereotype.Service;

@Service
public class PostVoteService {

    private final PostsVotesRepository postVotesDao;

    PostVoteService(PostsVotesRepository postVotesDao) {
        this.postVotesDao = postVotesDao;
    }

    public void delete(PostVote vote) {
        postVotesDao.delete(vote);
    }
}
