package com.example.furniture.controller;

import com.example.furniture.dto.SearchDto;
import com.example.furniture.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {

    private final ItemService itemService;

    @GetMapping
    @ResponseBody
    public List<SearchDto> getSearchResults(@RequestParam("text") final String text) {
        return itemService.findByText(text);
    }
}
