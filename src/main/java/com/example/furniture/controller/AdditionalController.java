package com.example.furniture.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static com.example.furniture.utils.PageValues.ABOUT;
import static com.example.furniture.utils.PageValues.ARTICLES;

@Controller
public class AdditionalController {

    @GetMapping("/about")
    public String aboutPage() {
        return ABOUT;
    }

    @GetMapping("/articles")
    public String articlesPage() {
        return ARTICLES;
    }

    @GetMapping("/articles/{id}")
    public String articlePage(@PathVariable("id") final Long id) {
        return "%s-%d".formatted(ARTICLES, id);
    }
}
