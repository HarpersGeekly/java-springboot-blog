package com.codeup.blog.springbootblog.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by RyanHarper on 11/2/17.
 */

@Controller // [Step 1]. Annotate.
//Controllers in Springboot will annotate url's to the method...not the class like in Servlets with extending urlPatterns
public class HelloWorldController {

//    @GetMapping("/hello") // [Step 2] hello() method will now respond to the /hello url.
//    @ResponseBody //[Step 3] Must have a response too, like how it was in Servlets.
//    // This will respond with everything inside the body of the hello() method
//    public String hello() {
//        return "Hello World!";
//    }
//
//    @GetMapping("/hello/{name}") //we don't do query strings anymore just path variables with wildcards like {name}
//    @ResponseBody
//    public String helloName(@PathVariable String name) { // {name} is the PathVariable
//        return "Hello, " + name + "!!";
//    }
//
//    @GetMapping("hello/{firstName}/{lastName}") // let's add another name in another path
//    @ResponseBody
//    public String helloFullName(@PathVariable String firstName, @PathVariable String lastName) {
//        return "<h1>Hello, " + firstName + " " + lastName + "!!</h1>";
//    }
//
//    @GetMapping("/square/{number}") // let's do a number. Spring takes care of the parsing of the query string for us!
//    @ResponseBody
//    public Integer square(@PathVariable Integer number) {
//        return number * number;
//    }

}
