package com.example.furniture.controller;

import com.example.furniture.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalUserController {

    private final CartService cartService;

    @ModelAttribute("currentUser")
    public String currentUser() {
        return getCurrentUsername();
    }

    @ModelAttribute("currentCartItemsCount")
    public int currentCartItemsCount() {
        return cartService.getCart(getCurrentUsername()).size();
    }

    public static String getCurrentUsername() {

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String)) {
            return auth.getName();
        }

        return null;
    }
}
