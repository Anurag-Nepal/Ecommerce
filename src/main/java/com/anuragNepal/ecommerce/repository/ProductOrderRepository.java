package com.anuragNepal.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.anuragNepal.ecommerce.entity.ProductOrder;
@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder, Long>{

}
