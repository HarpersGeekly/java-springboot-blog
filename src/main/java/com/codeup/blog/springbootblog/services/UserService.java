package com.codeup.blog.springbootblog.services;

import com.codeup.blog.springbootblog.Models.User;
import com.codeup.blog.springbootblog.repositories.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Created by RyanHarper on 11/7/17.
 */
@Service("usersSvc")
public class UserService {

    @Autowired
    private RolesRepository rolesDao;

    public boolean isLoggedIn() {
        boolean isAnonymousUser =
                SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken; //starts with an anonymous user
        return !isAnonymousUser; // authenticated
    }

    public User loggedInUser() {
        if (!isLoggedIn()) {
            return null;
        }
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // return a User who has authentication
    }

    public boolean isLoggedInAndOwnerMatchesUser(User user) {
        return isLoggedIn() && (loggedInUser().getId() == user.getId());
    }

    // Automatically logs in User:
    public void authenticate(User user) {
//        UserDetails userDetails = new UserWithRoles(user, Collections.emptyList()); // if not using roles.
        UserDetails userDetails = new UserWithRoles(user, rolesDao.ofUserWith(user.getUsername())); // if using roles
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(auth);
    }

    public void deleteSession() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }


}
