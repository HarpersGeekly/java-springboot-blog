package com.codeup.blog.springbootblog.controllers;

import com.codeup.blog.springbootblog.Models.User;
import com.codeup.blog.springbootblog.repositories.UsersRepository;
import com.codeup.blog.springbootblog.services.UserService;
import com.sun.xml.internal.bind.v2.TODO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user){

        // TODO: validate the input

        // double check that the username is not already in database
        User existingUser = usersDao.findByUsername(user.getUsername());
        if (existingUser != null) {
            return "redirect:/register";
        }
        // hash password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // save user in the database
        usersDao.save(user);
        return "redirect:/login";
    }

    // ============================================ PROFILE =======================================================

    @GetMapping("/profile")
    public String showProfilePage(Model viewModel) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        viewModel.addAttribute("user", user);
        return "users/profile";
    }

    @GetMapping("/profile/{id}")
    public String showOtherUsersProfilePage(@PathVariable Long id, Model viewModel) {
        User user = usersDao.findById(id);
        viewModel.addAttribute("showEditButton", userSvc.isLoggedInAndPostMatchesUser(user));
        viewModel.addAttribute("user", user);
        return "users/profile";
    }

}
