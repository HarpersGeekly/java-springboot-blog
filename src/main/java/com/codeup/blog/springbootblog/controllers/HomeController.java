package com.codeup.blog.springbootblog.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

/**
 * Created by RyanHarper on 11/2/17.
 */

@Controller
public class HomeController {

    @GetMapping("/home")
//    @ResponseBody
// Now that we use Thymeleaf, no need for ResponseBody because we're going to handle that with an html file
    public String showLandingPage() {
//        return "This is the landing page!";
        return "home"; //only use the name of the html file /templates/home.html
    }

    @GetMapping("/hello/{name}")
    public String sayHello(@PathVariable String name, Model viewModel) {

        ArrayList<String> names = new ArrayList<>();

        names.add("Fer");
        names.add("luis");
        names.add("zach");
        names.add("ryan");

        viewModel.addAttribute("names", names);
        viewModel.addAttribute("name", name);
        viewModel.addAttribute("rainy", true);
        return "hello";
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }


//    Create a HomeController class.
// This class should have one method with a GetMapping for /. It should return a string that says "This is the landing page!".
//    This will eventually be the page that users see when they first visit your page.
// Later on, when we learn about views, you can swap it out for something more fancy.
}
