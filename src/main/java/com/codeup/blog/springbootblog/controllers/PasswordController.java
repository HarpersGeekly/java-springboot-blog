package com.codeup.blog.springbootblog.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.codeup.blog.springbootblog.Models.FormatterUtil;
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

    @Autowired
    private FormatterUtil formatter;

    // ============================================ EDIT PASSWORD ====================================================

    @GetMapping("/profile/{id}/editPassword")
    public String showPasswordEditPage(@PathVariable Long id, Model viewModel) {
        // find the user in the database:
        User existingUser = usersDao.findById(id);
        viewModel.addAttribute("user", existingUser);
        viewModel.addAttribute("formatter", formatter);
        return "users/editPassword";
    }

    @PostMapping("profile/{id}/editPassword")
    public String changePassword(@PathVariable Long id, @Valid User user, Errors validation, Model viewModel,
                                 @RequestParam(name = "password_confirm") String passwordConfirmation,
                                 @RequestParam(name = "password") String password,
                                 RedirectAttributes redir) {

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
        redir.addFlashAttribute("success", success);
        redir.addFlashAttribute("successMessage", passwordSuccess);

        return "redirect:/profile";
    }

    // ============================================ FORGOT PASSWORD FORM ====================================================

    // Display forgotPassword page
    @RequestMapping(value = "/forgot", method = RequestMethod.GET)
    public String displayForgotPasswordPage(Model viewModel) {
        viewModel.addAttribute("formatter", formatter);
        return "users/forgotPassword";
    }

    // Process form submission from forgotPassword page
    @RequestMapping(value = "/forgot", method = RequestMethod.POST)
    public String processForgotPasswordForm(@RequestParam("email") String userEmail, HttpServletRequest request, Model viewModel) {

        boolean emailIsEmpty = userEmail.isEmpty();

        if (emailIsEmpty) {
            viewModel.addAttribute("isEmpty", emailIsEmpty);
            viewModel.addAttribute("errorMessage", "Please enter an email address");
        } else {

        User user = usersDao.findByEmail(userEmail);
        System.out.println(user.getUsername());
        System.out.println(user.getPasswordToken());

//      =================== DELETE OLD PASSWORD TOKENS BY USER ========================================
//        List<PasswordToken> oldPts = user.getPasswordToken();
//
//        if(oldPts != null) {
//            System.out.println("get here");
//            for(PasswordToken pt : oldPts) {
//                pt.getUser().setPasswordToken(null);
//                System.out.println("token?" + pt.getUser().getPasswordToken());
//                passwordDao.deleteByUserId(pt.getUser().getId());
//                System.out.println("delete?" + pt.getUser().getId());
//
//            }
//        }


            PasswordToken pt = new PasswordToken();
            // Set User
            pt.setUser(user);
//          // Set the timestamp.
            LocalDateTime ldt = LocalDateTime.now();
            LocalDateTime expired = ldt.plusMinutes(5);
            pt.setCreated_on(ldt);
            pt.setExpires_on(expired);
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
            viewModel.addAttribute("successMessage", "If a user exists with that email, a password reset link will be sent to the email address provided");
        }
        return "users/forgotPassword";

    }
    // ============================================ RESET PASSWORD FORM ================================================

    // Display form to reset password
    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    public String displayResetPasswordPage(Model viewModel,
                                           @RequestParam("token") String token, RedirectAttributes redirect) {

        PasswordToken pt = passwordDao.findByToken(token);
        Long id = pt.getUser().getId();
        User user = usersDao.findById(id);
        LocalDateTime ldt = LocalDateTime.now();
        LocalDateTime expires = pt.getExpires_on();

        if (user != null && ldt.isBefore(expires)) { // Token found in DB is not expired.
            viewModel.addAttribute("resetToken", token); // putting the token inside the form action url
        } else if (user != null && ldt.isAfter(expires)) { // Token found in DB is expired
            redirect.addFlashAttribute("isExpired", true);
            redirect.addFlashAttribute("errorMessage", "Oops!  This is an invalid password reset link.");
//            List<PasswordToken> oldPts = user.getPasswordToken();
            return "redirect:/login";
        }

        viewModel.addAttribute("user", user);
        viewModel.addAttribute("formatter", formatter);
        return "users/resetPassword";
    }

    // Process reset password form
    @RequestMapping(value = "/reset/{token}", method = RequestMethod.POST)
    public String setNewPassword(Model viewModel,
                                 @PathVariable String token,
                                 @RequestParam(name = "password") String password,
                                 RedirectAttributes redirect /* , @RequestParam Map<String, String> requestParams */) {

        // Find the user associated with the reset token
        PasswordToken pt = passwordDao.findByToken(token);
        Long id = pt.getUser().getId();
        User tokenUser = usersDao.findById(id);
        boolean isNotMinMax = (password.length() > 20 || password.length() < 8);

        if(password.isEmpty() || isNotMinMax) {
            viewModel.addAttribute("hasError", true);
            viewModel.addAttribute("resetToken", token);
            viewModel.addAttribute("errorMessage", "Passwords cannot be empty. Passwords must be between 8-20 characters");
            return "users/resetPassword";
        } else {

            // This should always be non-null but we check just in case
            if (tokenUser != null) {

                // Set new password
                tokenUser.setPassword(passwordEncoder.encode(password));

                // Set the reset token to null so it cannot be used again
//                tokenUser.setPasswordToken(null);
//                passwordDao.deleteById(pt.getId());

                // Save user
                usersDao.save(tokenUser);

                // In order to set a model attribute on a redirect, we must use
                // RedirectAttribute
                redirect.addFlashAttribute("hasReset", true);
                redirect.addFlashAttribute("successMessage", "You have successfully reset your password.  You may now login.");

                return "redirect:/login";

            } else {
                viewModel.addAttribute("errorMessage", "Oops!  This is an invalid password reset link.");
            }
        }
        return "users/resetPassword";
    }

    // Going to reset page without a token redirects to login page
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ModelAndView handleMissingParams(MissingServletRequestParameterException ex) {
        return new ModelAndView("redirect:/login");
    }
}