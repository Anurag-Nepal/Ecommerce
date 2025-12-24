package com.mdtalalwasim.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mdtalalwasim.ecommerce.entity.ProductOrder;
@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder, Long>{

}
