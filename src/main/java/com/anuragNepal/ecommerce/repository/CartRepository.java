package com.anuragNepal.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.anuragNepal.ecommerce.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long>{
	
	public Cart findByProductIdAndUserId(Long productId, Long userId);
	//public Cart findByProductAndUser(Product product, User user);

	public Long countByUserId(Long userId);

	public List<Cart> findByUserId(Long userId);
	
	public void deleteByUserId(Long userId);
}
