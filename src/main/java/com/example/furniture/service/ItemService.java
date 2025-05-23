package com.example.furniture.service;

import com.example.furniture.controller.GlobalUserController;
import com.example.furniture.dto.RateDto;
import com.example.furniture.dto.SearchDto;
import com.example.furniture.entity.Item;
import com.example.furniture.entity.ItemRating;
import com.example.furniture.entity.User;
import com.example.furniture.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.Long.parseLong;
import static java.math.RoundingMode.CEILING;
import static java.util.Map.Entry.comparingByKey;
import static java.util.Optional.ofNullable;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private static final BigDecimal MAX_RATING = new BigDecimal("5.00");
    private static final Pattern PATTERN = Pattern.compile("/img/([^/]+)/");

    private final CartService cartService;
    private final UserService userService;
    private final ItemRepository itemRepository;

    public List<SearchDto> findByText(final String text) {

        if (text == null || text.isEmpty()) {
            return List.of();
        }

        return itemRepository.findByText(text);
    }

    @Transactional
    public void rate(final Long itemId, final RateDto dto) {

        final long userId = parseLong(dto.getUserId());
        final long stars = parseLong(dto.getRating());
        final String comment = dto.getComment();

        itemRepository.rate(userId, itemId, stars, comment);

        final Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        final ItemRating itemRating = item.getItemRating();

        itemRating.setRating(
                itemRating.getRating()
                          .multiply(BigDecimal.valueOf(itemRating.getCount()))
                          .add(BigDecimal.valueOf(stars))
                          .divide(BigDecimal.valueOf(itemRating.getCount() + 1), CEILING)
                          .min(MAX_RATING)
        );
        itemRating.setCount(itemRating.getCount() + 1);
        item.setItemRating(itemRating);

        itemRepository.save(item);

        log.info("Пользователь {} оценил товар {}. Новый рейтинг: {}", userId, itemId, itemRating.getRating());
    }

    @Transactional
    public void like(final Long itemId, final boolean active) {

        try {
            final String username = GlobalUserController.getCurrentUsername();

            if (username == null) {

                log.error("Пользователь не авторизован");
                return;
            }

            final User user = userService.findByUsernameFetch(username);
            final Item item = findById(itemId);

            if (active) {
                user.getLikes().add(item);
            } else {
                user.getLikes().remove(item);
            }

            userService.save(user);
        } catch (final Exception e) {
            log.error("Ошибка like: {}", e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public Page<Item> findPageable(final String category, final Pageable pageable) {
        return itemRepository.findByImgContainsIgnoreCase(category, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Item> findLikesPageable(final Pageable pageable, final Long userId) {
        return itemRepository.findLikes(userId, pageable);
    }

    @Transactional(readOnly = true)
    public List<Item> findLikesByItemIds(final List<Long> ids, final Long userId) {
        return itemRepository.findLikes(userId, ids);
    }

    public Item findById(final Long id) {

        return itemRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Товар №" + id + " не существует")
        );
    }

    @Transactional(readOnly = true)
    public List<Item> findRelatedByItem(final Item item) {

        if (item == null) {
            return List.of();
        }

        final String category = ofNullable(item.getImg()).map(ItemService::extractCategory).orElse("");

        return itemRepository.findRelated(item.getId(), category, Pageable.ofSize(6)).getContent();
    }

    @Transactional
    public Item save(final Item item) {
        return itemRepository.save(item);
    }

    public Map<Item, Integer> getItemsFromCart(final String username) {

        return cartService.getCart(username)
                          .entrySet()
                          .stream()
                          .sorted(comparingByKey())
                          .collect(Collectors.toMap(
                                  e -> findById(e.getKey()),
                                  Map.Entry::getValue,
                                  (x, y) -> y, LinkedHashMap::new)
                          );
    }

    private static String extractCategory(final String imgPath) {

        final Matcher matcher = PATTERN.matcher(imgPath);

        if (matcher.find()) {
            return "/" + matcher.group(1) + "/";
        }

        return "";
    }
}
