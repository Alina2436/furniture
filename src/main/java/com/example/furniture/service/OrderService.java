package com.example.furniture.service;

import com.example.furniture.entity.Item;
import com.example.furniture.entity.Order;
import com.example.furniture.entity.OrderItem;
import com.example.furniture.entity.OrderItemId;
import com.example.furniture.entity.Payment;
import com.example.furniture.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.furniture.service.CartService.getTotalCartPrice;
import static java.time.Instant.now;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    public static final String NEW = "NEW";
    public static final String AWAITS_PAYMENT = "AWAITS_PAYMENT";
    public static final String PAYED = "PAYED";
    public static final String DELIVERY = "DELIVERY";
    public static final String DONE = "DONE";
    public static final int ACTIVE_TIME_MINUTES = 10;

    private final ItemService itemService;
    private final UserService userService;
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public Order findById(final Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Long findActiveIdByUsername(final String username) {
        return orderRepository.findActiveIdByUsername(username);
    }

    @Transactional(readOnly = true)
    public List<Order> findByUsername(final String username) {
        return orderRepository.findByUsername(username);
    }

    @Transactional
    public Order createOrder(final String username) {

        log.info("Checking out order for user {}", username);

        final Map<Item, Integer> items = itemService.getItemsFromCart(username);
        final BigDecimal totalPrice = getTotalCartPrice(items);

        if (totalPrice.compareTo(BigDecimal.ZERO) <= 0) {

            log.info("Order for user {} has no cart.", username);
            return null;
        }

        final Order o = Order.builder()
                             .userId(userService.findIdByUsername(username))
                             .status(NEW)
                             .totalPrice(totalPrice)
                             .created(now())
                             .build();

        final Set<OrderItem> orderItems = items.entrySet()
                                               .stream()
                                               .map(e -> new OrderItem(
                                                       OrderItemId.builder()
                                                                  .itemId(e.getKey().getId())
                                                                  .build(),
                                                       e.getKey(),
                                                       e.getValue(),
                                                       o
                                               ))
                                               .collect(Collectors.toCollection(LinkedHashSet::new));

        o.setOrderItems(orderItems);

        return orderRepository.save(o);
    }

    @Transactional
    public Order processOrder(final Order order, final String paymentType) {

        log.info("Processing order: {}", order);

        final Payment payment = Payment.builder()
                                       .order(order)
                                       .amount(order.getTotalPrice())
                                       .paymentType(paymentType)
                                       .activePeriodMinutes(ACTIVE_TIME_MINUTES)
                                       .status(AWAITS_PAYMENT)
                                       .created(now())
                                       .build();

        order.getPayments().add(payment);
        order.setStatus(DELIVERY);

        final Order processing = orderRepository.save(order);

        final Set<OrderItem> orderItems = processing.getOrderItems();

        orderItems.forEach(oi -> {

            final Item item = oi.getItem();
            item.setCount(Math.max(item.getCount() - oi.getCount(), 0));
            oi.setItem(itemService.save(item));
        });

        return orderRepository.save(processing);
    }

}
