package com.example.furniture.controller;

import com.example.furniture.dto.RateDto;
import com.example.furniture.entity.Item;
import com.example.furniture.service.ItemService;
import com.example.furniture.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static com.example.furniture.utils.PageValues.SHOP;
import static com.example.furniture.utils.PageValues.SHOP_SINGLE;

@Controller
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemsController {

    private final ItemService itemService;
    private final UserService userService;

    @GetMapping
    public String getItems(final Model model,
                           @RequestParam(defaultValue = "0") final int page,
                           @RequestParam(defaultValue = "9") final int size,
                           @RequestParam(defaultValue = "itemRating.rating") final String sort,
                           @RequestParam(defaultValue = "") final String category) {

        final Sort by = Sort.by(sort);
        final Pageable pageRequest = PageRequest.of(page, size, "name".equals(sort) ? by.ascending() : by.descending());
        final String username = GlobalUserController.getCurrentUsername();

        if (username != null) {

            model.addAttribute(
                    "likes",
                    itemService.findLikesPageable(pageRequest, userService.findIdByUsername(username))
                               .stream()
                               .map(Item::getId)
                               .toList()
            );
        }

        final Page<Item> items;

        if ("likes".equals(category) && username != null) {
            items = itemService.findLikesPageable(pageRequest, userService.findIdByUsername(username));
        } else {
            items = itemService.findPageable(category, pageRequest);
        }

        model.addAttribute("items", items);
        model.addAttribute("sort", sort);
        model.addAttribute("category", category);

        return SHOP;
    }

    @ResponseBody
    @PostMapping("/{id}/like")
    public void likeItem(@PathVariable("id") final Long id, @RequestParam final boolean active) {
        itemService.like(id, active);
    }

    @ResponseBody
    @PostMapping("/{id}/rate")
    public void likeItem(@PathVariable("id") final Long id, @RequestBody final RateDto dto) {
        itemService.rate(id, dto);
    }

    @GetMapping("/{id}")
    public String getItem(@PathVariable("id") final Long id, final Model model) {

        final Item item = itemService.findById(id);
        final List<Item> relatedItems = itemService.findRelatedByItem(item);
        final String username = GlobalUserController.getCurrentUsername();

        if (username != null) {

            model.addAttribute(
                    "likes",
                    itemService.findLikesByItemIds(
                                       relatedItems.stream().map(Item::getId).toList(),
                                       userService.findIdByUsername(username)
                               )
                               .stream()
                               .map(Item::getId)
                               .toList()
            );
        }

        model.addAttribute("item", item);
        model.addAttribute("related", relatedItems);

        return SHOP_SINGLE;
    }
}
