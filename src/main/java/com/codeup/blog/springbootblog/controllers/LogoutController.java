package com.codeup.blog.springbootblog.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

/**
 * Created by RyanHarper on 11/15/17.
 */
@Controller
@RequestMapping("/logout")
public class LogoutController {

    @RequestMapping(method= RequestMethod.POST)
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/posts";
    }
}

//    From stackoverflow: I think the common problem when using @SessionAttributes
// is after you invalidate your current session, Spring MVC attach the model attributes
// back into the new session -- hence causing the impression it never invalidates
//
//        You can check the value of JSESSIONID before & after you invalidate it.
// You will get a brand new JSESSIONID, yet previous model attributes are attached straight into the new session
//
//        I found myself having to do this to wipe a model attribute of name "counter"
// from session after invalidating it
//
//@RequestMapping(value="/invalidate", method=RequestMethod.POST)
//public String invalidate(HttpSession session, Model model) {
//        session.invalidate();
//        if(model.containsAttribute("counter")) model.asMap().remove("counter");
//        return "redirect:/counter";
//        }
//        If you have plenty attributes, ofcourse you can try wiping everything off using
//
//        model.asMap().clear();
//        But in my opinion better approach is to invalidate using a different controller that doesn't have @SessionAttribute on it. Hence whatever model attributes other controllers have won't be attached straight into the new session. Eg:
//
//@Controller
//@RequestMapping("/logout")
//public class LogoutController {
//
//    @RequestMapping(method=RequestMethod.POST)
//    public String logout(HttpSession session) {
//        session.invalidate();
//        return "redirect:/login";
//    }
//}