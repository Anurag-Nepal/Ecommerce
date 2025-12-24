package com.mdtalalwasim.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mdtalalwasim.ecommerce.entity.Order;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
}

