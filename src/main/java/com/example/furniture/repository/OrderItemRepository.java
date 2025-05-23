package com.example.furniture.repository;

import com.example.furniture.entity.OrderItem;
import com.example.furniture.entity.OrderItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemId> {
}