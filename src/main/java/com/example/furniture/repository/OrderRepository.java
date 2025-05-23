package com.example.furniture.repository;

import com.example.furniture.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select max(o.id) from Order o where exists(select 1 from User u where o.userId = u.id and u.username = ?1) and o.status in ('NEW', 'AWAITS_PAYMENT')")
    Long findActiveIdByUsername(final String username);

    @Query("select o from Order o where o.user.username = ?1 order by o.created desc")
    List<Order> findByUsername(final String username);
}