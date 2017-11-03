package com.codeup.blog.springbootblog.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Created by RyanHarper on 11/2/17.
 */

@Controller
public class RollDiceController {
        private int counter = 0;

    @GetMapping("/roll-dice")
    public String rollDice() {
        return "roll-dice";
    }

    @GetMapping("/roll-dice/{guess}")
    public String rollDiceCompare(@PathVariable int guess, Model viewModel) {

        int random = (int)(Math.random() * 6 + 1);
        counter++;
        viewModel.addAttribute("randomNumber", random);
        viewModel.addAttribute("guess", guess);
        viewModel.addAttribute("counter", counter);
        return "roll-dice";
    }
}
