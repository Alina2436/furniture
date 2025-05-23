package com.example.furniture.service;

import com.example.furniture.entity.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final RedisTemplate<String, Map<Long, Integer>> redisTemplate;

    public void addItem(final String username, final Long itemId, final int count) {

        final Map<Long, Integer> cart = getCart(username);

        cart.putIfAbsent(itemId, 0);
        cart.merge(itemId, count, Integer::sum);

        redisTemplate.opsForValue().set(getCartKey(username), cart);
    }

    public Map<Long, Integer> getCart(final String username) {

        return ofNullable(redisTemplate.opsForValue().get(getCartKey(username)))
                .map(
                        m -> m.entrySet()
                              .stream()
                              .collect(Collectors.toMap(
                                      e -> Long.valueOf(String.valueOf(e.getKey())),
                                      Map.Entry::getValue
                              ))
                )
                .orElse(new HashMap<>());
    }

    public void deleteItem(final String username, final Long itemId, final int count) {

        final Map<Long, Integer> cart = getCart(username);

        if (cart.containsKey(itemId) && cart.get(itemId) > count) {
            cart.merge(itemId, count, (c1, c2) -> Math.max(c1 - c2, 0));
        } else {
            cart.remove(itemId);
        }

        redisTemplate.opsForValue().set(getCartKey(username), cart);

        log.info("Удален элемент из корзины для {} в количестве {}: [{}]", username, count, itemId);
    }

    private static String getCartKey(final String username) {
        return "cart:" + username;
    }

    public static BigDecimal getTotalCartPrice(final Map<Item, Integer> items) {

        return items.entrySet()
                    .stream()
                    .map(e -> Map.entry(e.getKey().getPrice(), e.getValue()))
                    .flatMap(e -> Stream.of(e.getKey().multiply(BigDecimal.valueOf(e.getValue()))))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
