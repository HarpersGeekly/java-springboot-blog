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
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDateTime;

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

    // ============================================= LOGGING IN USER ===================================================

    @GetMapping("/login")
    public String showLoginForm() {
//        System.out.println(new BCryptPasswordEncoder().encode("pass"));
        return "/users/login";
    }

    // ========================================== REGISTER USER ========================================================

    @GetMapping("/register")
    public String showRegisterForm(Model model){
        model.addAttribute("user", new User());
        return "/users/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid User user,
                               Errors validation,
                               Model viewModel,
                               @RequestParam(name = "password_confirm") String passwordConfirmation) {

        // Validation:
        // @Valid User user now calls @ModelAttribute User user first/instead and calls the validations!
        // @RequestParam = asks for the value in the form field called "password_confirm" assigns it to String passwordConfirmation

        // double check that the username is not already in database:
        User existingUser = usersDao.findByUsername(user.getUsername());
        if (existingUser != null) {
            validation.rejectValue(
                    "username",
                    "user.username",
                    "Username is already taken.");
        }

        //compare passwords:
        if (!passwordConfirmation.equals(user.getPassword())) {
            validation.rejectValue(
                    "password",
                    "user.password",
                    "Your passwords do not match.");
        }

        // if there are errors, show the form again.
        if (validation.hasErrors()) {
            viewModel.addAttribute("errors", validation);
            viewModel.addAttribute("user", user);
            return "users/register"; // not a redirect:/register because I'd lose the information.
        }

        // Otherwise, if all is good:
        // if password is valid, hash the password:
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // set the users join date:
        user.setDate(LocalDateTime.now());
        // save user in the database:
        usersDao.save(user);
        // redirect them to the login page.
        // The login page has a @{/login} action that talks to the SecurityConfiguration class.
        // Spring handles the logging in.
        return "redirect:/login";
    }

    // ================================================= PROFILE =======================================================

    @GetMapping("/profile")
    public String showProfilePage(Model viewModel) {
        User userLoggedIn = userSvc.loggedInUser(); // Grab the loggedInUser from the service and assign them a name, userLoggedIn.
        viewModel.addAttribute("isOwnProfile", true); // boolean condition returns true, will always be true because they're loggedin.
        viewModel.addAttribute("profileUser", usersDao.findOne(userLoggedIn.getId()));
        return "users/profile";
    }

    @GetMapping("/profile/{id}")
    public String showOtherUsersProfilePage(@PathVariable Long id, Model viewModel) {
        User user = usersDao.findById(id); // find the User from the id in the url profile/{id}/edit
        viewModel.addAttribute("isOwnProfile", userSvc.isLoggedIn() && user.equals(userSvc.loggedInUser()));
        // ^this is a boolean^, true, but false if passing another id.
        viewModel.addAttribute("profileUser", user);
        return "users/profile";
    }

    // ============================================== EDIT PROFILE =====================================================

    @GetMapping("/profile/{id}/edit")
    public String showProfileEditPage(@PathVariable Long id, Model viewModel) {
        // find the user in the database:
        User existingUser = usersDao.findById(id);
        // pass it to the view: pre-populate the form with the values from that user.
        viewModel.addAttribute("user", existingUser);
        return "users/edit";
    }

    @PostMapping("profile/{id}/edit")
    public String update(@PathVariable Long id, @Valid User user, Errors validation, Model viewModel,
                         @RequestParam(name = "username") String username,
                         @RequestParam(name = "email") String email,
//                         @RequestParam(name = "password") String password, // is this needed?
                         @RequestParam(name = "date") LocalDateTime date) {

        // double check that the username is not already in database:
        User existingUser = usersDao.findByUsername(user.getUsername());
        if (existingUser != null) {
            validation.rejectValue(
                    "username",
                    "user.username",
                    "Username is already taken.");
        }

        if (validation.hasErrors()) {
            viewModel.addAttribute("errors", validation);
            viewModel.addAttribute("user", user);
            return "users/edit";
        }

        user.setUsername(username);
        user.setEmail(email);
//        user.setPassword(password);
        user.setDate(date); // The date remains the same as the original join date.
        user.setId(id);
        usersDao.save(user);
        return "redirect:/users/profile/{id}";
    }

    // =========================================== DELETE PROFILE ACCOUNT ==============================================

    @PostMapping("/profile/{id}/delete")
    public String delete(@PathVariable long id, Model viewModel, HttpSession session) {
        usersDao.delete(id);
        // even if I delete a user, the navbar "Username's Profile" is still reading a session.
        // How do I delete, then cancel/logout a session?
        // I built a LogoutController.
        LogoutController logout = new LogoutController();
        logout.logout(session);
        return "redirect:/posts";
    }
}
