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
        boolean isAnonymousUser = //starts with an anonymous user...gets authenticated:
                SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken;
        return !isAnonymousUser; // now is not an anonymous user!
    }

    public User loggedInUser() {
        if (! isLoggedIn()) {// if you are an anonymous user...return null.
            return null;
        } // otherwise return a User who has authentication!
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public boolean isLoggedInAndPostMatchesUser(User user) {
        return isLoggedIn() && (loggedInUser().getId() == user.getId());
    }
}
