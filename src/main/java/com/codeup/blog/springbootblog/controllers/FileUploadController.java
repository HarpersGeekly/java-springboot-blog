package com.codeup.blog.springbootblog.controllers;

import com.codeup.blog.springbootblog.Models.User;
import com.codeup.blog.springbootblog.repositories.UsersRepository;
import com.codeup.blog.springbootblog.services.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

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
                @RequestParam(name = "croppedImage") MultipartFile uploadedFile,
                Model model
        ) {
            User loggedInUser = userSvc.loggedInUser(); // is a user from the session
            loggedInUser = usersDao.findById(loggedInUser.getId()); //now is a user from the database

//            String filename = uploadedFile.getOriginalFilename(); ////comment this out
            String filepath = Paths.get(uploadPath, loggedInUser.getProfilePicture()).toString();
//            String filepath = Paths.get(uploadPath, filename).toString();////comment this out

            File destinationFile = new File(filepath);

//            User user = usersDao.findOne(loggedInUser.getId());
//            loggedInUser.setProfilePicture(filepath); //we deleted this.////comment this out
//            usersDao.save(loggedInUser);//we deleted this.////comment this out
            try {
                uploadedFile.transferTo(destinationFile);
                model.addAttribute("message", "File successfully uploaded!");
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("message", "Oops! Something went wrong! " + e);
            }
            return loggedInUser;
        }
    }

//    @GetMapping("img-display")
//    public String showUploadFileForm() {
//        return "img-display";
//    }

//    @PostMapping("/profile/image")
//    @ResponseBody
//    public String saveFile(
//            //@RequestParam(name = "img-display") MultipartFile uploadedFile,
//            @RequestParam(value = "url", required = false) String url,
//            @RequestParam(value = "id", required = false) Long userId) {
//        System.out.println("[saveFile] url: " + url);
//
//        User loggedInUser = userSvc.loggedInUser();
//        User user = usersDao.findOne(loggedInUser.getId());
//        user.setProfilePicture(url);
//        usersDao.save(user);
//        // INJECT THE USER REPOSITORY
//        // CALL THE METHOD findOne USING THE userId variable
//        // images.setProfilePic(url);
//        // usersRepository.save(user);
//
////            String filename = uploadedFile.getOriginalFilename();
////            String filepath = Paths.get(uploadPath, filename).toString();
////            File destinationFile = new File(filepath);
////            try {
////                uploadedFile.transferTo(destinationFile);
////                model.addAttribute("message", "File successfully uploaded!");
////            } catch (IOException e) {
////                e.printStackTrace();
////                model.addAttribute("message", "Oops! Something went wrong! " + e);
////            }
//        return "redirect:/profile";
//    }