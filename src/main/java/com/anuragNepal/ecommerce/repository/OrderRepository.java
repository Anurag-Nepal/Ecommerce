package com.anuragNepal.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.anuragNepal.ecommerce.entity.Order;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
}

