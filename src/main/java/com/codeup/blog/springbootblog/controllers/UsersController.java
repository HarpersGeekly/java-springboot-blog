package com.codeup.blog.springbootblog.controllers;

import com.codeup.blog.springbootblog.Models.*;
import com.codeup.blog.springbootblog.repositories.CategoriesRepository;
import com.codeup.blog.springbootblog.repositories.HitCountsRepository;
import com.codeup.blog.springbootblog.repositories.RolesRepository;
import com.codeup.blog.springbootblog.repositories.UsersRepository;
import com.codeup.blog.springbootblog.services.CommentService;
import com.codeup.blog.springbootblog.services.PostService;
import com.codeup.blog.springbootblog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private HitCountsRepository hitCountsDao;
    private RolesRepository rolesDao;
    private FormatterUtil formatter = new FormatterUtil();

    @Autowired
    public UsersController(UsersRepository usersDao,
                           PasswordEncoder passwordEncoder,
                           UserService userSvc,
                           CategoriesRepository categoriesDao,
                           CommentService commentSvc,
                           PostService postSvc,
                           HitCountsRepository hitCountsDao,
                           RolesRepository rolesDao) {
        this.usersDao = usersDao;
        this.passwordEncoder = passwordEncoder;
        this.userSvc = userSvc;
        this.categoriesDao = categoriesDao;
        this.commentSvc = commentSvc;
        this.postSvc = postSvc;
        this.hitCountsDao = hitCountsDao;
        this.rolesDao = rolesDao;
    }

    // ============================================= LOGGING IN USER ===================================================

    @GetMapping("/login")
    public String showLoginForm(HttpServletRequest request, Model viewModel) {
//        String referrer = request.getHeader("Referer");
//        request.getSession().setAttribute("url_prior_login", referrer);
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

        if(userSvc.isLoggedIn()) {
            return "redirect:/profile/" + userSvc.loggedInUser().getId() + "/" + userSvc.loggedInUser().getUsername();
        }

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

        User existingEmail = usersDao.findByEmail(user.getEmail());
        if (existingEmail != null) {
            validation.rejectValue(
                    "email",
                    "user.email",
                    "Email is already used.");
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
        user.setBio(null);
        // save user in the database:
        usersDao.save(user);
        // save user in Roles as user
        rolesDao.save(UserRole.user(user));
        // redirect them to the login page.
        // The login page has a @{/login} action that talks to the SecurityConfiguration class.
        // Spring handles the logging in.
        userSvc.authenticate(user); // now that I have authenticate in the UserService, it automatically logs in the registered user,
        // so I no longer have to reroute them to the login page.
        Long id = user.getId();
        String username = user.getUsername();
        return "redirect:/profile/" + id + "/" + username;
    }

    // ================================================= PROFILE =======================================================

    @GetMapping("/profile")
    public String showProfilePage() {
        User userLoggedIn = userSvc.loggedInUser();
        return "redirect:profile/" + userLoggedIn.getId() + "/" + userLoggedIn.getUsername();
    }

    @GetMapping("/profile/{id}/{username}")
    public String showOtherUsersProfilePage(@PathVariable Long id, Model viewModel) {
        User user = usersDao.findById(id);

        HitCount userHitCount = user.getHitCount();
        if(userHitCount == null) {
            HitCount newHitCount = new HitCount();
            newHitCount.setUser(user);
            userHitCount = newHitCount;
        }
        userHitCount.setProfileCount(userHitCount.getProfileCount() + 1);
        hitCountsDao.save(userHitCount);

        List<Comment> comments = commentSvc.findAllByUserIdOrderByDateDesc(user.getId());
        forEachUserComment(viewModel, comments);

        List<String> roles = rolesDao.ofUserWith(user.getUsername());
        for(String role : roles) {
            if (role.equals("ROLE_USER")) {
                viewModel.addAttribute("ownerIsUser", role);
            }
            if (role.equals("ROLE_EDITOR")) {
                viewModel.addAttribute("ownerIsEditor", role);
            }
            if (role.equals("ROLE_ADMIN")) {
                viewModel.addAttribute("ownerIsAdmin", role);
            }
        }

        if(userSvc.loggedInUser() != null) {
            User loggedInUser = userSvc.loggedInUser();
            List<String> myRoles = rolesDao.ofUserWith(loggedInUser.getUsername());
            for (String role : myRoles) {
                if (role.equals("ROLE_ADMIN")) {
                    viewModel.addAttribute("isLoggedInUserAdmin", role);
                }
            }
        }
        //if posts are empty:
        boolean postsAreEmpty = user.getPosts().isEmpty();
        boolean commentsAreEmpty = user.getComments().isEmpty();
        //if user is banned:
        boolean isBanned = user.isBanned();

        List<Post> posts = postSvc.postsByResultSetIndexPage();
        viewModel.addAttribute("posts", posts);
        for(Post post : posts) {
            boolean isDisabled = post.isDisabled();
            viewModel.addAttribute("isDisabled", isDisabled);
        }
        viewModel.addAttribute("postsAreEmpty", postsAreEmpty);
        viewModel.addAttribute("commentsAreEmpty", commentsAreEmpty);
        viewModel.addAttribute("isOwnProfile", userSvc.isLoggedIn() && user.equals(userSvc.loggedInUser()));
        viewModel.addAttribute("profileUser", user);
        viewModel.addAttribute("isBanned", isBanned);
        viewModel.addAttribute("categories", categoriesDao.findAll());
        viewModel.addAttribute("karma", usersDao.totalKarmaByUser(user.getId()));
        viewModel.addAttribute("comments", comments);
        viewModel.addAttribute("formatter", formatter);
        return "users/profile";
    }

    private void forEachUserComment(Model viewModel, List<Comment> comments) {
        for(Comment cmt : comments) {
            Post post = cmt.getPost();
            viewModel.addAttribute("post", post);
            if(cmt.getParentComment() != null) {
                boolean isParentComment = cmt.isParentComment(cmt);
                viewModel.addAttribute("isParentComment", isParentComment);
            }
        }
    }

    // =========================================== PROFILE ARCHIVED POSTS ==============================================

    @GetMapping("/profile/{id}/archived")
    public String userArchivedPosts(@PathVariable Long id, Model viewModel) {

        User user = usersDao.findById(id);
        List<Post> posts = postSvc.postsByUser(user.getId());
        viewModel.addAttribute("user", user);
        viewModel.addAttribute("isBanned", user.isBanned());
        viewModel.addAttribute("posts", posts);
        viewModel.addAttribute("isOwnProfile", userSvc.isLoggedIn() && user.equals(userSvc.loggedInUser()));
        viewModel.addAttribute("formatter", formatter);
        return "users/userArchivedPosts";
    }

    // ============================================== EDIT PROFILE =====================================================

    @GetMapping("/profile/{id}/edit")
    public String showProfileEditPage(@PathVariable Long id, Model viewModel) {
        // find the user in the database:
        User existingUser = usersDao.findById(id);
        // pass it to the view: pre-populate the form with the values from that user ie: th:field="{user.username}"
        viewModel.addAttribute("user", existingUser);
        viewModel.addAttribute("formatter", formatter);
        return "users/editUser";
    }

    @PostMapping("profile/{id}/edit")
    public String update(@PathVariable Long id, @Valid User user, Errors validation,
                         @RequestParam(name = "username") String username, Model viewModel, RedirectAttributes redir) {

        // user who is already in the database and on the form:
        User existingUser = usersDao.findById(id);

        HitCount userHitCount = existingUser.getHitCount();
        hitCountsDao.save(userHitCount);

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
        if(user.getBio().isEmpty()) {
            user.setBio(null);
        }
        user.setHitCount(userHitCount);
        userHitCount.setProfileCount(userHitCount.getProfileCount());
        usersDao.save(user);

        User loggedInUser = userSvc.loggedInUser();
        List<String> roles = rolesDao.ofUserWith(loggedInUser.getUsername());
        for(String role : roles) {
            if (role.equals("ROLE_ADMIN") && (!loggedInUser.getId().equals(user.getId()))) {
                userSvc.authenticate(loggedInUser);
                boolean success = (!validation.hasErrors());
                String profileSuccess = user.getUsername() + "'s profile updated using ADMIN rights.";
                redir.addFlashAttribute("isLoggedInUserAdmin", role);
                redir.addFlashAttribute("success", success);
                redir.addFlashAttribute("successMessage", profileSuccess);
                return "redirect:/profile/" + user.getId() + "/" + user.getUsername();
            }
        }
        for(String role : roles) {
            if (role.equals("ROLE_ADMIN")) {
                redir.addFlashAttribute("isLoggedInUserAdmin", role);
            }
        }

        userSvc.authenticate(user);
        boolean success = (!validation.hasErrors());
        String profileSuccess = "Profile Updated!";
        redir.addFlashAttribute("success", success);
        redir.addFlashAttribute("successMessage", profileSuccess);
        return "redirect:/profile/" + loggedInUser.getId() + "/" + loggedInUser.getUsername();
    }


    // =========================================== DELETE PROFILE ACCOUNT ==============================================

    @PostMapping("/profile/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redir) {

        System.out.println("get to delete");

        User user = usersDao.findById(id);
        long role_id = rolesDao.findRoleIdByUserId(user.getId());
        rolesDao.delete(role_id);
        usersDao.delete(id);
        userSvc.deleteSession();
        redir.addFlashAttribute("successDelete", usersDao.findById(id) == null);
        redir.addFlashAttribute("successMessage", "Sorry to see you go! Your account has been deactivated.");
        return "redirect:/login";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model viewModel) {
        User loggedInUser = userSvc.loggedInUser();
        if(loggedInUser == null) {
            return "redirect:/login";
        } else {
            List<String> roles = rolesDao.ofUserWith(loggedInUser.getUsername());
            for (String role : roles) {
                if (!role.equals("ROLE_ADMIN")) {
                    return "redirect:/posts";
                }
            }
        }

        viewModel.addAttribute("formatter", formatter);
        viewModel.addAttribute("posts", postSvc.findAll());
        List<User> users = (List<User>) usersDao.findAll();
        viewModel.addAttribute("users", users);
        viewModel.addAttribute("rolesDao", rolesDao);
        viewModel.addAttribute("usersDao", usersDao);
        return "users/adminDashboard";
    }

//================================================ DISABLE AND ENABLE POSTS ============================================
//======================================================================================================================


    @PostMapping("/profile/{id}/ban")
    public String disablePost(@PathVariable long id) {
        User user = usersDao.findOne(id);
        user.ban();
        usersDao.save(user);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/profile/{id}/unban")
    public String enablePost(@PathVariable long id) {
        User user = usersDao.findOne(id);
        user.unban();
        usersDao.save(user);
        return "redirect:/admin/dashboard";
    }


//================================================ GRANT PERMISSIONS ===================================================
//======================================================================================================================

    @PostMapping("admin/dashboard/grant-admin/{id}")
    public @ResponseBody
    UserRole grantAdminPermissions(@PathVariable Long id) {
        User user = usersDao.findById(id);
        UserRole ur = rolesDao.findUserRoleByUserId(user.getId());
        ur.setRole("ROLE_ADMIN");
        ur.setUserId(user.getId());
        rolesDao.save(ur);
        return ur;
//        The @ResponseBody annotation tells a controller that the object returned is automatically serialized into
//        JSON and passed back into the HttpResponse object.
//        In browser console we can see a response similar to: {id: 7, userId: 1, role: "ROLE_ADMIN"}
    }

}
