package com.example.furniture.controller;

import com.example.furniture.dto.LoginDto;
import com.example.furniture.dto.RegisterDto;
import com.example.furniture.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.stream.Collectors;

import static com.example.furniture.utils.PageValues.LOGIN;
import static com.example.furniture.utils.PageValues.REGISTER;
import static java.lang.System.lineSeparator;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage(final Model model) {

        model.addAttribute("user", new LoginDto());
        return LOGIN;
    }

    @GetMapping("/register")
    public String registerPage(final Model model) {

        model.addAttribute("user", new RegisterDto());
        return REGISTER;
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("user") final LoginDto user, final BindingResult res, final Model model,
                        final HttpServletRequest request) {

        log.info("Login user: {}", user.getUsername());

        if (res.hasErrors()) {

            processBindingErrors(res, model);

            return LOGIN;
        }

        try {
            userService.login(user.getUsername(), user.getPassword(), request);
        } catch (final Exception e) {

            processSimpleError(e.getMessage(), model);

            return LOGIN;
        }

        return "redirect:/";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute final RegisterDto user, final BindingResult res, final Model model) {

        if (res.hasErrors()) {

            processBindingErrors(res, model);

            return REGISTER;
        }

        try {
            userService.register(user);
        } catch (final Exception e) {

            processSimpleError(e.getMessage(), model);

            return REGISTER;
        }

        return "redirect:/" + LOGIN;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationException(final MethodArgumentNotValidException e, final Model model) {

        final String errorMessage = e.getBindingResult()
                                     .getFieldErrors()
                                     .stream()
                                     .map(FieldError::getDefaultMessage)
                                     .collect(Collectors.joining("," + lineSeparator()));

        model.addAttribute("error", errorMessage);

        return REGISTER;
    }

    public static void processBindingErrors(final BindingResult result, final Model model) {

        processSimpleError(result.getAllErrors()
                                 .stream()
                                 .map(ObjectError::toString)
                                 .collect(Collectors.joining(", ")), model);
    }

    private static void processSimpleError(final String message, final Model model) {

        log.error(message);
        model.addAttribute("error", message);
    }
}
