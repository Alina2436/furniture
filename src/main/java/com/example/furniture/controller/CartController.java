package com.example.furniture.controller;

import com.example.furniture.entity.Item;
import com.example.furniture.service.CartService;
import com.example.furniture.service.ItemService;
import com.example.furniture.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

import static com.example.furniture.service.CartService.getTotalCartPrice;
import static com.example.furniture.utils.PageValues.CART;
import static com.example.furniture.utils.PageValues.LOGIN;
import static com.example.furniture.utils.PageValues.SHOP;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final ItemService itemService;
    private final OrderService orderService;

    @ResponseBody
    @PostMapping
    public void addToCart(@RequestParam("itemId") final Long itemId, @RequestParam("count") final int count) {

        log.info("Добавление товара {} в корзину в количестве {}", itemId, count);

        try {
            final String username = GlobalUserController.getCurrentUsername();

            if (username == null) {

                final String message = "Пользователь не авторизован";

                log.error(message);
                throw new IllegalStateException(message);
            }

            cartService.addItem(username, itemId, count);
        } catch (final Exception e) {

            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @ResponseBody
    @GetMapping("/hasItem")
    public boolean hasItem(@RequestParam("itemId") final Long itemId) {

        final String username = GlobalUserController.getCurrentUsername();

        if (username == null) {
            return false;
        }

        final Map<?, Integer> cart = cartService.getCart(username);
        log.info("Cart for {}: {}", username, cart);

        return (cart.containsKey(itemId) && cart.get(itemId) != 0)
               || (cart.containsKey(itemId.toString()) && cart.get(itemId.toString()) != 0);
    }

    @GetMapping
    public String cartPage(final Model model) {

        try {
            final String username = GlobalUserController.getCurrentUsername();

            if (username == null) {

                model.addAttribute("error", "Сессия истекла. Войдите повторно");
                return LOGIN;
            }

            final Long orderId;

            if ((orderId = orderService.findActiveIdByUsername(username)) != null) {
                model.addAttribute("orderId", orderId);
            }

            final Map<Item, Integer> items = itemService.getItemsFromCart(username);

            model.addAttribute("cart", items);
            model.addAttribute("totalPrice", getTotalCartPrice(items));

            return CART;
        } catch (final Exception e) {

            log.error(e.getMessage(), e);
            return "redirect:/" + SHOP;
        }
    }

    @PostMapping("/remove")
    public String removeItem(@RequestParam("itemId") final Long itemId, @RequestParam("count") final int count,
                             final Model model) {

        try {
            final String username = GlobalUserController.getCurrentUsername();

            if (username == null) {

                model.addAttribute("error", "Сессия истекла. Войдите повторно");
                return LOGIN;
            }

            cartService.deleteItem(username, itemId, count);
        } catch (final Exception e) {

            log.error(e.getMessage(), e);
        }

        return "redirect:/" + CART;
    }

}
