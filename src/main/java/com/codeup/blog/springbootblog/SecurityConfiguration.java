package com.codeup.blog.springbootblog;

import com.codeup.blog.springbootblog.services.MyCustomLoginSuccessHandler;
import com.codeup.blog.springbootblog.services.UserDetailsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * Created by RyanHarper on 11/8/17.
 */

//GIVEN IN CURRICULUM
//Finally, we need to create a configuration class to determine several things

//Which pages will require authentication
  //      Which pages are available to anyone
    //    The path to the login page, and
      //  The hashing algorithm we'll use to store passwords

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsLoader userDetails;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    } //plain text to hash

    @Bean // this Bean is part of the redirect-to-previous page functionality
    public AuthenticationSuccessHandler successHandler() {
        return new MyCustomLoginSuccessHandler("/profile");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // login
                .formLogin()
                    .loginPage("/login")
                .defaultSuccessUrl("/profile") // after logging in, go to user's profile page
                // this is new:
                .successHandler(successHandler()) // this is part of the redirect-to-previous page functionality

                    .permitAll() // Anyone can go to the login page
                .and()

                // non-logged in users
                .authorizeRequests()
                .antMatchers("/posts", "/login", "/register", "/profile/{id}") // anyone can see the home and register page
                .permitAll()
                .and()

                // logout
                .logout()
                .logoutSuccessUrl("/posts") //after logout, go to posts page.
//                .logoutSuccessUrl("/login?logout") // original convention
                .and()

                //restricted area
                .authorizeRequests()
                .antMatchers("/posts/create",
                        "/posts/{id}/edit",
                        "/posts/{id}/delete",
                        "/posts/{id}/comment",
                        "/posts/{postId}/comment/{commentId}/delete",
                        "/posts/{id}/comment/{commentId}",
                        "/posts/{postId}/comment/{commentId}/reply/{replyId}/delete",
                        "/profile",
                        "/profile/{id}/edit",
                        "/profile/{id}/delete",
                        "/profile/{id}/editPassword"
                ) // only authenticated (logged in) users can create/edit posts
                .authenticated()
        ;
    }

//============= :) WE DID ALL THE SECURITY STEPS AND MADE CLASSES JUST TO USE THIS NEXT METHOD :) ==================

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetails) // how to find a user by its username
                .passwordEncoder(passwordEncoder()); // how to encode/verify a password
    }
}