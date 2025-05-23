package com.example.furniture.repository;

import com.example.furniture.dto.SearchDto;
import com.example.furniture.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Override
    @Transactional(readOnly = true)
    Optional<Item> findById(final Long id);

    Page<Item> findByImgContainsIgnoreCase(final String category, final Pageable pageable);

    @Query("select i from Item i join UserLike ul on i.id = ul.id.itemId where ul.id.userId = ?1")
    Page<Item> findLikes(final Long userId, final Pageable pageable);

    @Query("select i from Item i join UserLike ul on i.id = ul.id.itemId and i.id in ?2 where ul.id.userId = ?1")
    List<Item> findLikes(final Long userId, final List<Long> ids);

    @Query("select i from Item i where i.id != ?1 and upper(i.img) like upper(concat('%', ?2, '%'))")
    Page<Item> findRelated(final Long itemId, final String category, final Pageable pageable);

    @Modifying
    @Query(
            value = "insert into user_rating_history(user_id, item_id, stars, comment) values (?1, ?2, ?3, ?4)",
            nativeQuery = true
    )
    void rate(final Long userId, final Long itemId, final Long stars, final String comment);

    @Query(
            value = """
                    select i.id as id, i.img as img, i.name as name, i.brand as brand, i.price as price
                    from items i
                    where i.name ilike concat('%', ?1, '%')
                       or i.brand ilike concat('%', ?1, '%')
                       or i.description ilike concat('%', ?1, '%')
                    limit 10;
                    """,
            nativeQuery = true
    )
    @Transactional(readOnly = true)
    List<SearchDto> findByText(final String text);
}
