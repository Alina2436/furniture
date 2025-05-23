package com.example.furniture.controller;

import com.example.furniture.dto.OrderProcessDto;
import com.example.furniture.entity.Order;
import com.example.furniture.entity.OrderItem;
import com.example.furniture.entity.Payment;
import com.example.furniture.service.CartService;
import com.example.furniture.service.OrderService;
import com.example.furniture.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Set;

import static com.example.furniture.controller.AuthController.processBindingErrors;
import static com.example.furniture.service.OrderService.AWAITS_PAYMENT;
import static com.example.furniture.service.OrderService.DONE;
import static com.example.furniture.service.OrderService.NEW;
import static com.example.furniture.service.OrderService.PAYED;
import static com.example.furniture.utils.PageValues.CART;
import static com.example.furniture.utils.PageValues.LOGIN;
import static com.example.furniture.utils.PageValues.ORDER;
import static java.time.ZoneId.systemDefault;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Comparator.comparing;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private static final DateTimeFormatter FORMATTER = ofPattern("HH:mm dd.MM.yyyy").withZone(systemDefault());

    private final CartService cartService;
    private final UserService userService;
    private final OrderService orderService;

    @PostMapping
    public String checkout(final Model model) {

        try {
            final String username = GlobalUserController.getCurrentUsername();

            if (username == null) {

                model.addAttribute("error", "Сессия истекла. Войдите повторно");
                return LOGIN;
            }

            final Order o = orderService.createOrder(username);

            if (o == null) {
                return "redirect:/" + CART;
            }

            model.addAttribute("checkout", true);
            model.addAttribute("order", o);
            model.addAttribute("created", FORMATTER.format(o.getCreated()));
            model.addAttribute("count", o.getOrderItems().stream().map(OrderItem::getCount).reduce(0, Integer::sum));
            model.addAttribute("orderDto", new OrderProcessDto());
        } catch (final Exception e) {

            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        return ORDER;
    }

    @GetMapping("/{id}")
    public String showOrder(@PathVariable("id") final Long id, final Model model) {

        final String username = GlobalUserController.getCurrentUsername();

        if (username == null) {

            model.addAttribute("error", "Сессия истекла. Войдите повторно");
            return LOGIN;
        }

        final Order o = orderService.findById(id);

        if (!Objects.equals(o.getUserId(), userService.findIdByUsername(username))) {

            model.addAttribute("error", "Недостаточно прав доступа к этой странице");
            return LOGIN;
        }

        model.addAttribute("order", o);
        model.addAttribute("created", FORMATTER.format(o.getCreated()));
        model.addAttribute("orderDto", new OrderProcessDto());
        model.addAttribute("checkout", NEW.equals(o.getStatus()));
        o.getPayments().stream().max(comparing(Payment::getId)).ifPresent(p -> model.addAttribute("payment", p));
        model.addAttribute(
                "count",
                o.getOrderItems()
                 .stream()
                 .map(OrderItem::getCount)
                 .reduce(0, Integer::sum)
        );

        return ORDER;
    }

    @PostMapping("/{id}/process")
    public String process(@PathVariable("id") final Long id, @ModelAttribute("orderDto") final OrderProcessDto orderDto,
                          final Model model, final BindingResult result) {

        final Order o = orderService.findById(id);

        if (result.hasErrors()) {

            processBindingErrors(result, model);

            model.addAttribute("order", o);
            model.addAttribute("orderDto", new OrderProcessDto());
            model.addAttribute("checkout", NEW.equals(o.getStatus()));
            model.addAttribute("created", FORMATTER.format(o.getCreated()));
            model.addAttribute(
                    "count",
                    o.getOrderItems()
                     .stream()
                     .map(OrderItem::getCount)
                     .reduce(0, Integer::sum)
            );

            return ORDER;
        }

        if (AWAITS_PAYMENT.equals(o.getStatus()) || DONE.equals(o.getStatus()) || PAYED.equals(o.getStatus())) {
            log.error("Заказ {} не может быть переоформлен", id);
        }

        final String username = GlobalUserController.getCurrentUsername();

        if (username == null) {

            model.addAttribute("error", "Сессия истекла. Войдите повторно");
            return LOGIN;
        }

        o.setDeliveryType(orderDto.getDeliveryType());

        final Order processed = orderService.processOrder(o, orderDto.getPaymentType());
        final Set<OrderItem> orderItems = processed.getOrderItems();

        orderItems.forEach(i -> cartService.deleteItem(username, i.getId().getItemId(), i.getCount()));

        model.addAttribute("order", processed);
        model.addAttribute("created", FORMATTER.format(processed.getCreated()));
        model.addAttribute("count", orderItems.stream().map(OrderItem::getCount).reduce(0, Integer::sum));

        processed.getPayments().stream().max(comparing(Payment::getId)).ifPresent(
                p -> model.addAttribute("payment", p)
        );

        return ORDER;
    }

}
