package com.codeup.blog.springbootblog.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by RyanHarper on 11/2/17.
 */

@Controller
public class MathController {

    @GetMapping("/add/{number1}/and/{number2}")
    @ResponseBody
    public Double addition(@PathVariable Double number1, @PathVariable Double number2) {
        return number1 + number2;
    }

    @GetMapping("/subtract/{number1}/from/{number2}")
    @ResponseBody
    public Double subtract(@PathVariable Double number1, @PathVariable Double number2) {
        return number1 - number2;
    }

    @GetMapping("/multiply/{number1}/and/{number2}")
    @ResponseBody
    public Double multiply(@PathVariable Double number1, @PathVariable Double number2) {
        return number1 * number2;
    }

    @GetMapping("/divide/{number1}/by/{number2}")
    @ResponseBody
    public Double divide(@PathVariable Double number1, @PathVariable Double number2) {
        return number1 / number2;
    }


}
