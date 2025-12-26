package com.anuragNepal.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.anuragNepal.ecommerce.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	List<Product> findByIsActiveTrue();

	List<Product> findByProductCategory(String category);
	
	List<Product> findByIsActiveTrueOrderByCreatedAtDesc();

}
