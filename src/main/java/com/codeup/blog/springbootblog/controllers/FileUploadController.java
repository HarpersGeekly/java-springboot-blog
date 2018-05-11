package com.codeup.blog.springbootblog.controllers;

import com.codeup.blog.springbootblog.Models.User;
import com.codeup.blog.springbootblog.repositories.UsersRepository;
import com.codeup.blog.springbootblog.services.UserService;
import com.codeup.blog.springbootblog.services.UserWithRoles;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.Paths;
import java.util.Collections;

@Controller
public class FileUploadController {

    private final UsersRepository usersDao;
    private final UserService userSvc;

    public FileUploadController(UsersRepository usersDao, UserService userSvc) {
        this.usersDao = usersDao;
        this.userSvc = userSvc;
    }

        @Value("${file-upload-path}")
        private String uploadPath;

//        @GetMapping("/profile/edit/fileupload")
//        public String showUploadFileForm() {
//            return "users/editUser";
//        }

        @PostMapping("/profile/edit/fileupload")
        public @ResponseBody User saveFile(
                @RequestParam(name = "croppedImage") MultipartFile uploadedFile) {
            //let exception be thrown for maxsize and solve the issue in the ajax.done with if fails, then show html in the partial

            System.out.println("get to FileUploadController");

            User loggedInUser = userSvc.loggedInUser(); // is a user from the session
            loggedInUser = usersDao.findById(loggedInUser.getId()); //now is a user from the database
            String filepath = Paths.get(uploadPath, loggedInUser.profilePicturePath()).toString(); //first is path, second is additional string to be joined to form the final path string
            File destinationFile = new File(filepath);
            loggedInUser.updateProfilePicture();
            usersDao.save(loggedInUser);

            //Reset the current principal loggedinUser with the changes applied so that the profile picture won't show the default placeholder after they reload the page.
            UserWithRoles principal = new UserWithRoles(loggedInUser, Collections.emptyList());
            Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            try {
                uploadedFile.transferTo(destinationFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return loggedInUser;
        }
    }