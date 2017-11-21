package com.codeup.blog.springbootblog.services;

import com.codeup.blog.springbootblog.Models.User;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collections;

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

    // Automatically logs in User:
    public void authenticate(User user) {
        // I'm not using roles so I'm using an empty list for the roles
        UserDetails userDetails = new UserWithRoles(user, Collections.emptyList());
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(auth);
    }
}
