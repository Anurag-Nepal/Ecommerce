package com.mdtalalwasim.ecommerce.service;

import com.mdtalalwasim.ecommerce.entity.ProductOrder;
import com.mdtalalwasim.ecommerce.entity.ProductOrderRequest;

public interface ProductOrderService {
	
	public ProductOrder saveProductOrder(Long id, ProductOrderRequest productOrderRequest);
}
