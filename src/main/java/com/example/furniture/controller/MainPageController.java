package com.example.furniture.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

import static com.example.furniture.utils.PageValues.INDEX;

@Controller
public class MainPageController {

    @GetMapping("/")
    public String getIndex(final Model model) {

        model.addAttribute("year", LocalDate.now().getYear());
        return INDEX;
    }
}
