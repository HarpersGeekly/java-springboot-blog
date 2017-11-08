package com.codeup.blog.springbootblog.services;

import com.codeup.blog.springbootblog.Models.User;
import com.codeup.blog.springbootblog.Models.UserWithRoles;
import com.codeup.blog.springbootblog.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Created by RyanHarper on 11/8/17.
 */
// GIVEN IN CURRICULUM.
// We will need to define a service that spring security will use to
// load the authentication and authorization information of users.

@Service
public class UserDetailsLoader implements UserDetailsService { // UserDetailsService is a built-in Spring interface

    private final UsersRepository usersDao;

    @Autowired
    public UserDetailsLoader(UsersRepository usersDao) {
        this.usersDao = usersDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = usersDao.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("No user found for " + username);
        }

        return new UserWithRoles(user, Collections.emptyList());
    }
}
