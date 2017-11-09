package com.codeup.blog.springbootblog.controllers;

import com.codeup.blog.springbootblog.Models.User;
import com.codeup.blog.springbootblog.repositories.UsersRepository;
import com.codeup.blog.springbootblog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Created by RyanHarper on 11/8/17.
 */

@Controller
public class UsersController {

    private UsersRepository usersDao;
    private PasswordEncoder passwordEncoder;
    private UserService userSvc;

    @Autowired
    public UsersController(UsersRepository usersDao, PasswordEncoder passwordEncoder, UserService userSvc) {
        this.usersDao = usersDao;
        this.passwordEncoder = passwordEncoder;
        this.userSvc = userSvc;
    }

    // ======================================== LOGGING IN ===================================================

    @GetMapping("/login")
    public String showLoginForm() {
//        System.out.println(new BCryptPasswordEncoder().encode("pass"));
        return "/users/login";
    }

    // ========================================= REGISTER  ========================================================

    @GetMapping("/register")
    public String showRegisterForm(Model model){
        model.addAttribute("user", new User());
        return "/users/register";
    }

    @PostMapping("/users/register")
    public String registerUser(@ModelAttribute User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        usersDao.save(user);
        return "redirect:/login";
    }
}
