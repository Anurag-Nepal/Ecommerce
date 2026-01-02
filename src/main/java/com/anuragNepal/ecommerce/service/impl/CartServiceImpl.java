package com.anuragNepal.ecommerce.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.anuragNepal.ecommerce.entity.Cart;
import com.anuragNepal.ecommerce.entity.Product;
import com.anuragNepal.ecommerce.entity.User;
import com.anuragNepal.ecommerce.repository.CartRepository;
import com.anuragNepal.ecommerce.repository.ProductRepository;
import com.anuragNepal.ecommerce.repository.UserRepository;
import com.anuragNepal.ecommerce.service.CartService;

@Service
public class CartServiceImpl implements CartService{

	@Autowired
	CartRepository cartRepository;
	
	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Override
	public Cart saveCart(Long productId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();

        Double unitPrice = product.getDiscountPrice() != null ? product.getDiscountPrice() : product.getProductPrice();
        Cart cartStatus = cartRepository.findByProductIdAndUserId(productId, userId);
        Cart cart;
        if(ObjectUtils.isEmpty(cartStatus)) {
            cart = new Cart();
            cart.setUser(user);
            cart.setProduct(product);
            cart.setQuantity(1);
            cart.setTotalPrice(unitPrice);
        } else {
            cart = cartStatus;
            cart.setQuantity(cart.getQuantity() + 1);
            cart.setTotalPrice(cart.getQuantity() * unitPrice);
        }
        return cartRepository.save(cart);
    }

	@Override
	public List<Cart> getCartsByUser(Long userId) {
	 List<Cart> carts = cartRepository.findByUserId(userId);
	 //System.out.println("CARTS :"+carts.toString());
	 
	 Double totalOrderPrice = 0.0;
	 
		// because of my totalPrice colum is transient, so we dont get totalPrice
		// directly from database table.
		// we need to fetch it
	 List<Cart> updatedCartList = new ArrayList<>();
		for (Cart cart : carts) {
			Double unitPrice = cart.getProduct().getDiscountPrice() != null ? cart.getProduct().getDiscountPrice() : cart.getProduct().getProductPrice();
			Double totalPrice = unitPrice * cart.getQuantity();
			cart.setTotalPrice(totalPrice);
			totalOrderPrice = totalOrderPrice + totalPrice;
			cart.setTotalOrderPrice(totalOrderPrice);
			updatedCartList.add(cart);
		}
		
	 
	 
		return updatedCartList;
	}

	@Override
	public Long getCounterCart(Long userId) {
		return cartRepository.countByUserId(userId);
	}

	@Override
	public Boolean updateCartQuantity(String symbol, Long cartId) {
		
		Optional<Cart> cart = cartRepository.findById(cartId);
		int quantity;
		if(cart.isPresent()) {
			if(symbol.equalsIgnoreCase("decrease")) {
				Integer dbQty = cart.get().getQuantity();
				quantity	= dbQty - 1;
				if(quantity <= 0) {
					cartRepository.deleteById(cartId);
					return true;
				}
				
			}else {
				Integer dbQty = cart.get().getQuantity();
				quantity	= dbQty + 1;
			}
			cart.get().setQuantity(quantity);
			cartRepository.save(cart.get());
		}
		
		
		
		return false;
	}

	@Override
	public void deleteCartByUser(Long userId) {
		cartRepository.deleteByUserId(userId);
	}

}
