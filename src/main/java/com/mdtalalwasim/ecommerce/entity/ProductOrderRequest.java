package com.mdtalalwasim.ecommerce.entity;

import lombok.Data;

@Data
public class ProductOrderRequest {
	
	private String firstName;

	private String lastName;

	private String email;

	private String mobile;

	private String address;

	private String city;

	private String state;

	private String pinCode;
	
	
	private String paymentType;

}
