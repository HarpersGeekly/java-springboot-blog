package com.codeup.blog.springbootblog.controllers;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.codeup.blog.springbootblog.Models.MailSender;
import com.codeup.blog.springbootblog.Models.PasswordToken;
import com.codeup.blog.springbootblog.Models.User;
import com.codeup.blog.springbootblog.repositories.PasswordRepository;
import com.codeup.blog.springbootblog.repositories.UsersRepository;
import com.codeup.blog.springbootblog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PasswordController {

    @Autowired
    private UserService userSvc;

    @Autowired
    private UsersRepository usersDao;

    @Autowired
    private PasswordRepository passwordDao;

    @Autowired
    private MailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ============================================ EDIT PASSWORD ====================================================

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

    // ============================================ FORGOT PASSWORD FORM ====================================================

    // Display forgotPassword page
    @RequestMapping(value = "/forgot", method = RequestMethod.GET)
    public String displayForgotPasswordPage(Model viewModel) {
        return "users/forgotPassword";
    }

    // Process form submission from forgotPassword page
    @RequestMapping(value = "/forgot", method = RequestMethod.POST)
    public String processForgotPasswordForm(@RequestParam("email") String userEmail, HttpServletRequest request, Model viewModel) {

        // Lookup user in database by e-mail
        User user = usersDao.findByEmail(userEmail);

        if (user == null) {
            viewModel.addAttribute("hasNotSent", true);
            viewModel.addAttribute("errorMessage", "We didn't find an account for that e-mail address.");
        } else {

            PasswordToken pt = new PasswordToken();
            // Set User
            pt.setUser(user);
//          // Set the timestamp.
            pt.setCreated_on(Date.from(Instant.now()));
            // Generate random 36-character string token for reset password
            pt.setToken(UUID.randomUUID().toString());
            // Save token to database
            passwordDao.save(pt);

            String appUrl = request.getScheme() + "://" + request.getServerName() + ":8080";

            // Email message - I SENT MYSELF THE MESSAGE?
            mailSender.sendMail("support@demo.com", user.getEmail(),
                    "Password Reset Request",
                    "To reset your password, click the link below:\n" + appUrl
                    + "/reset?token=" + pt.getToken() + ". If you did not request a password change, you may ignore " +
                            "this email as it is entirely safe to do so. Thank you.");

            // Add success message to view
            viewModel.addAttribute("hasSent", true);
            viewModel.addAttribute("successMessage", "A password reset link has been sent to " + userEmail);
        }
        return "users/forgotPassword";

    }
    // ============================================ RESET PASSWORD FORM ================================================

    // Display form to reset password
    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    public String displayResetPasswordPage(Model viewModel, @RequestParam("token") String token) {

        PasswordToken pt = passwordDao.findByToken(token);
        Long id = pt.getUser().getId();
        User user = usersDao.findById(id);

        if (user != null) { // Token found in DB and not expired.
            viewModel.addAttribute("resetToken", token); // putting the token inside the form action url
        } else { // Token not found in DB
            viewModel.addAttribute("errorMessage", "Oops!  This is an invalid password reset link.");
            return "redirect:/login";
        }

        return "users/resetPassword";
    }

    // Process reset password form
    @RequestMapping(value = "/reset/{token}", method = RequestMethod.POST)
    public String setNewPassword(Model viewModel, @PathVariable String token, @RequestParam Map<String, String> requestParams, RedirectAttributes redir) {
        // Find the user associated with the reset token
        PasswordToken pt = passwordDao.findByToken(token);
        Long id = pt.getUser().getId();
        User user = usersDao.findById(id);

        // This should always be non-null but we check just in case
        if (user != null) {

            // Set new password
            user.setPassword(passwordEncoder.encode(requestParams.get("password")));

            // Set the reset token to null so it cannot be used again
            user.setPasswordToken(null);
            passwordDao.deleteById(pt.getId());

            // Save user
            usersDao.save(user);

            // In order to set a model attribute on a redirect, we must use
            // RedirectAttribute
            redir.addFlashAttribute("hasReset", true);
            redir.addFlashAttribute("successMessage", "You have successfully reset your password.  You may now login.");
//            viewModel.addAttribute("successMessage", "You have successfully reset your password.  You may now login.");

            return "redirect:/login";

        } else {
            viewModel.addAttribute("errorMessage", "Oops!  This is an invalid password reset link.");
        }

        return "users/resetPassword";
    }

    // Going to reset page without a token redirects to login page
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ModelAndView handleMissingParams(MissingServletRequestParameterException ex) {
        return new ModelAndView("redirect:/login");
    }
}