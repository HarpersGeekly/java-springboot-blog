package com.codeup.blog.springbootblog.services;

import com.codeup.blog.springbootblog.Models.User;
import com.codeup.blog.springbootblog.repositories.PostsRepository;
import com.codeup.blog.springbootblog.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Created by RyanHarper on 11/7/17.
 */
@Service("usersSvc")
public class UserService {

    public boolean isLoggedIn() {
        boolean isAnonymousUser =
                SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken;
        return !isAnonymousUser;
    }

    public User loggedInUser() {
        if (! isLoggedIn()) {
            return null;
        }
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public boolean isLoggedInAndPostMatchesUser(User user) {
        return isLoggedIn() && (loggedInUser().getId() == user.getId());
    }



}
