package com.anuragNepal.ecommerce.service;

import com.anuragNepal.ecommerce.entity.ProductOrder;
import com.anuragNepal.ecommerce.entity.ProductOrderRequest;

public interface ProductOrderService {
	
	public ProductOrder saveProductOrder(Long id, ProductOrderRequest productOrderRequest);
}
