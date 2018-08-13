package com.codeup.blog.springbootblog.controllers;

import com.codeup.blog.springbootblog.Models.Post;
import com.codeup.blog.springbootblog.Models.User;
import com.codeup.blog.springbootblog.repositories.CategoriesRepository;
import com.codeup.blog.springbootblog.repositories.UsersRepository;
import com.codeup.blog.springbootblog.services.CommentService;
import com.codeup.blog.springbootblog.services.PostService;
import com.codeup.blog.springbootblog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by RyanHarper on 11/8/17.
 */

@Controller
public class UsersController {

    private UsersRepository usersDao;
    private PasswordEncoder passwordEncoder;
    private UserService userSvc;
    private CategoriesRepository categoriesDao;
    private CommentService commentSvc;
    private PostService postSvc;

    @Autowired
    public UsersController(UsersRepository usersDao,
                           PasswordEncoder passwordEncoder,
                           UserService userSvc,
                           CategoriesRepository categoriesDao,
                           CommentService commentSvc,
                           PostService postSvc) {
        this.usersDao = usersDao;
        this.passwordEncoder = passwordEncoder;
        this.userSvc = userSvc;
        this.categoriesDao = categoriesDao;
        this.commentSvc = commentSvc;
        this.postSvc = postSvc;
    }

    // ============================================= LOGGING IN USER ===================================================

//    @GetMapping("/login")
//    public String showLoginForm(Model viewModel) {
//        viewModel.addAttribute("user", new User());
//        return "/users/login";
//    }

    @GetMapping("/login")
    public String showLoginForm(HttpServletRequest request, Model viewModel) {
        String referrer = request.getHeader("Referer");
        request.getSession().setAttribute("url_prior_login", referrer);
        viewModel.addAttribute("user", new User());
        return "/users/login";
    }

    // ======================================================================================================
    // 1/18/18: Trying to further validate Login. But, we don't even get here. Where do we further validate?
    // ======================================================================================================
//    @PostMapping("/login")
//    public String login(@Valid User user,
//                        Errors validation,
//                        Model viewModel,
//                        @RequestParam(value = "username") String username,
//                        @RequestParam(value = "password") String password) {
//
//        System.out.println("postmapping login method");
//        User existingUser = usersDao.findByUsername(username);
//        if (existingUser == null && user.getUsername().equals(username)) {
//            validation.rejectValue(
//                    "username",
//                    "user.username",
//                    "There is no user with that username.");
//        }
//
//        if (!password.equals(existingUser.getPassword())) {
//            validation.rejectValue(
//                    "password",
//                    "user.password",
//                    "Password is incorrect for that user.");
//        }
//
//        if (validation.hasErrors()) {
//            viewModel.addAttribute("errors", validation);
//            viewModel.addAttribute("user", user);
//            viewModel.addAttribute("username", username);
//            viewModel.addAttribute("password", password);
//            return "/users/login";
//        }
//
//        return "redirect:/profile";
//    }

    // ========================================== REGISTER USER ========================================================

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
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
        userSvc.authenticate(user); // now that I have authenticate in the UserService, it automatically logs in the registered user,
        // so I no longer have to reroute them to the login page.
        return "redirect:/profile";
    }

    // ================================================= PROFILE =======================================================

    @GetMapping("/profile")
    public String showProfilePage(Model viewModel) {
        User userLoggedIn = userSvc.loggedInUser(); // Grab the loggedInUser from the service and assign them a name, userLoggedIn.
        User user = usersDao.findById(userLoggedIn.getId());
        long totalKarma = usersDao.totalKarmaByUser(user.getId());
        viewModel.addAttribute("karma", totalKarma);
            //if posts are empty
        boolean postsAreEmpty = user.getPosts().isEmpty();
//       viewModel.addAttribute("activeIn", categoriesDao.)
        viewModel.addAttribute("posts", postSvc.postsByUserLimited(userLoggedIn.getId()));
        viewModel.addAttribute("postsAreEmpty", postsAreEmpty);
        viewModel.addAttribute("isOwnProfile", true); // boolean condition returns true, will always be true because they're loggedin.
        viewModel.addAttribute("profileUser", user);
        viewModel.addAttribute("categories", categoriesDao.findAll());
        return "users/profile";
    }

    @GetMapping("/profile/{id}")
    public String showOtherUsersProfilePage(@PathVariable Long id, Model viewModel) {
        User user = usersDao.findById(id); // find the User from the id in the url profile/{id}/edit
        long totalKarma = usersDao.totalKarmaByUser(user.getId());
        viewModel.addAttribute("karma", totalKarma);

        //if posts are empty:
        boolean postsAreEmpty = user.getPosts().isEmpty();
        viewModel.addAttribute("posts", postSvc.postsByUserLimited(user.getId()));
        viewModel.addAttribute("postsAreEmpty", postsAreEmpty);
        viewModel.addAttribute("isOwnProfile", userSvc.isLoggedIn() && user.equals(userSvc.loggedInUser()));
        // ^this is a boolean^, true, but false if passing another id.
        viewModel.addAttribute("profileUser", user);
        viewModel.addAttribute("categories", categoriesDao.findAll());
        return "users/profile";
    }

    // ============================================== EDIT PROFILE =====================================================

    @GetMapping("/profile/{id}/edit")
    public String showProfileEditPage(@PathVariable Long id, Model viewModel) {
        // find the user in the database:
        User existingUser = usersDao.findById(id);

        // pass it to the view: pre-populate the form with the values from that user ie: th:field="{user.username}"
        viewModel.addAttribute("user", existingUser);
        return "users/editUser";
    }

    @PostMapping("profile/{id}/edit")
    public String update(@PathVariable Long id, @Valid User user, Errors validation,
                         @RequestParam(name = "username") String username, Model viewModel) {

        // user who is already in the database and on the form:
        User existingUser = usersDao.findById(id);

        // Handle issue when someone leaves username unchanged it won't default to "username already taken"
        if (!existingUser.getUsername().equals(username)) {

            User updatedUser = usersDao.findByUsername(username);

            if (updatedUser != null) {
                validation.rejectValue(
                        "username",
                        "user.username",
                        "Username is already taken.");
            }
        }

        if (validation.hasErrors()) {
            viewModel.addAttribute("errors", validation);
            viewModel.addAttribute("user", user);
            return "users/editUser";
        }

        user.setId(id);
        usersDao.save(user);
        userSvc.authenticate(user); // Programmatically login the new user

        boolean success = (!validation.hasErrors());
        String profileSuccess = "Profile Updated!";
        viewModel.addAttribute("success", success);
        viewModel.addAttribute("successMsg", profileSuccess);
        return "users/editUser";
    }


    // ============================================ CHANGE PASSWORD ====================================================

    @GetMapping("/profile/{id}/editPassword")
    public String showPasswordEditPage(@PathVariable Long id, Model viewModel) {
        // find the user in the database:
        User existingUser = usersDao.findById(id);
        viewModel.addAttribute("user", existingUser);
        return "users/editPassword";
    }

    @PostMapping("profile/{id}/editPassword")
    public String changePassword(@PathVariable Long id, @Valid User user, Errors validation, Model viewModel,
                                 @RequestParam(name = "password_confirm") String passwordConfirmation,
                                 @RequestParam(name = "password") String password) {

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
            return "users/editPassword";
        }

        // hash the password:
        user.setPassword(passwordEncoder.encode(password));
        user.setId(id);
        usersDao.save(user);
        userSvc.authenticate(user);

        // alert the user that their password has changed.
        boolean success = (!validation.hasErrors());
        String passwordSuccess = "You have successfully updated your password!";
        viewModel.addAttribute("success", success);
        viewModel.addAttribute("successMessage", passwordSuccess);

        return "users/editPassword";
    }

    // =========================================== DELETE PROFILE ACCOUNT ==============================================

    @PostMapping("/profile/{id}/delete")
    public String delete(@PathVariable Long id, Model viewModel, HttpSession session) {

        usersDao.delete(id);
        // even if I delete a user, the navbar "ThisUsername's Profile" is still reading a session.
        // How do I delete, then cancel/logout a session?
        // I built a new controller, LogoutController, and called it in here.
        LogoutController logout = new LogoutController();
        logout.logout(session);

        // alert the user that they are no more...
        boolean success = (usersDao.findById(id) == null);
        String deleteSuccess = "Sorry to see you go! Your account has been deactivated.";
        viewModel.addAttribute("user", new User()); //empty user. Still needed for view.
        viewModel.addAttribute("successDelete", success);
        viewModel.addAttribute("successMessage", deleteSuccess);

//        return "redirect:/posts";
        return "users/editUser";
    }

    @GetMapping("/profile/{id}/archived")
    public String userArchivedPosts(@PathVariable Long id, Model viewModel) {

        User user = usersDao.findById(id);
        List<Post> posts = postSvc.postsByUser(user.getId());
        viewModel.addAttribute("posts", posts);
        viewModel.addAttribute("isOwnProfile", userSvc.isLoggedIn() && user.equals(userSvc.loggedInUser()));
        return "users/userArchivedPosts";
    }
}
