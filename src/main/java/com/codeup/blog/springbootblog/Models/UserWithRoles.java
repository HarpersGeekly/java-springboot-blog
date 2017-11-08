package com.codeup.blog.springbootblog.Models;

import com.codeup.blog.springbootblog.Models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;

/**
 * Created by RyanHarper on 11/8/17.
 */
// GIVEN IN CURRICULUM.
// The security package will need a single model with the information required to authenticate and authorize users.
// Therefore this single model is the union of both users and roles. This class must implement the UserDetails interface.

public class UserWithRoles extends User implements UserDetails { // UserDetails is a built-in Spring interface.

    private List<String> userRoles;

    public UserWithRoles(User user, List<String> userRoles) {
        super(user); // call the copy constructor defined in the User model
        this.userRoles = userRoles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roles = StringUtils.collectionToCommaDelimitedString(userRoles);
        return AuthorityUtils.commaSeparatedStringToAuthorityList(roles);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
