package com.givi.ecommerce.orderservice.repository;

import com.givi.ecommerce.orderservice.entity.Order;
import com.givi.ecommerce.orderservice.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository
        extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"items"})
    List<Order> findByUserId(
            Long userId
    );

    @EntityGraph(attributePaths = {"items"})
    Page<Order> findByUserId(
            Long userId,
            Pageable pageable
    );

    @Override
    @EntityGraph(attributePaths = {"items"})
    Optional<Order> findById(
            Long id
    );

    long countByStatus(
            OrderStatus status
    );
}