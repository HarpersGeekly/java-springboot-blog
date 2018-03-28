package com.codeup.blog.springbootblog.controllers.LessonIntro;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ColorController {

    @GetMapping("/color")
    public String pickColor() {
        return "colorPlay";
    }

    @PostMapping("/color")
    public String seeColor(@RequestParam(name = "pickcolorText") String pickcolorText,
                           @RequestParam(name = "pickcolorBackground") String pickcolorBackground, Model viewModel) {
        viewModel.addAttribute("textColor", pickcolorText);
        viewModel.addAttribute("backgroundColor", pickcolorBackground);
        return "colorSee";
    }
}
