package com.example.furniture.controller;

import com.example.furniture.dto.ProfileDto;
import com.example.furniture.entity.OrderItem;
import com.example.furniture.entity.User;
import com.example.furniture.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.example.furniture.utils.PageValues.LOGIN;
import static com.example.furniture.utils.PageValues.PROFILE;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public String profilePage(final Model model) {

        final String username = GlobalUserController.getCurrentUsername();

        if (username == null) {

            model.addAttribute("error", "Сессия истекла. Войдите повторно");
            return LOGIN;
        }

        final User user = userService.findByUsernameFetch(username);

        model.addAttribute("user", user);
        model.addAttribute("userDto", buildDto(user));
        model.addAttribute("purchased", getPurchasedOrderItems(user));

        return PROFILE;
    }

    @GetMapping("/users/{id}")
    public String profilePage(@PathVariable("id") final Long id, final Model model) {

        final String username = GlobalUserController.getCurrentUsername();

        if (username == null) {

            model.addAttribute("error", "Сессия истекла. Войдите повторно");
            return LOGIN;
        }

        if (!Objects.equals(userService.findIdByUsername(username), id)) {

            model.addAttribute("error", "Недостаточно прав доступа к этой странице");
            return LOGIN;
        }

        final User user = userService.findByUsernameFetch(username);

        model.addAttribute("user", user);
        model.addAttribute("userDto", buildDto(user));
        model.addAttribute("purchased", getPurchasedOrderItems(user));

        return PROFILE;
    }

    @PostMapping("/users/{id}")
    public String updateProfile(@PathVariable("id") final Long id,
                                @ModelAttribute("userDto") @Valid final ProfileDto userDto,
                                final BindingResult result,
                                final Model model) {

        final String username = GlobalUserController.getCurrentUsername();

        if (username == null || !userService.findIdByUsername(username).equals(id)) {

            model.addAttribute("error", "Сессия истекла или недостаточно прав");
            return LOGIN;
        }

        if (result.hasErrors()) {
            model.addAttribute("user", userService.findByUsernameFetch(username));
            return PROFILE;
        }

        userService.updateUser(id, userDto);
        return "redirect:/users/" + id;
    }

    private static ProfileDto buildDto(final User user) {

        final ProfileDto dto = new ProfileDto();

        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        dto.setLastname(user.getLastname());
        dto.setPatronymic(user.getPatronymic());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setAge(user.getAge());

        return dto;
    }

    private static List<OrderItem> getPurchasedOrderItems(final User user) {

        return user.getOrders().stream()
                   .filter(o -> o.getStatus().equals("DONE") || o.getStatus().equals("DELIVERY"))
                   .flatMap(o -> o.getOrderItems().stream())
                   .collect(Collectors.toMap(
                           oi -> oi.getItem().getId(),
                           Function.identity(),
                           (o1, o2) -> o1
                   ))
                   .values()
                   .stream()
                   .toList();
    }
}
