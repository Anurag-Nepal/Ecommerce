package com.anuragNepal.ecommerce.service;

import java.util.List;

import com.anuragNepal.ecommerce.entity.Cart;

public interface CartService {
	
	public Cart saveCart(Long productId, Long userId);
	
	public List<Cart> getCartsByUser(Long userId);
	
	public Long getCounterCart(Long userId);

	public Boolean updateCartQuantity(String symbol, Long cartId);
	
	public void deleteCartByUser(Long userId);
	
}
