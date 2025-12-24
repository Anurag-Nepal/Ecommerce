package com.mdtalalwasim.ecommerce.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mdtalalwasim.ecommerce.entity.Cart;
import com.mdtalalwasim.ecommerce.entity.Order;
import com.mdtalalwasim.ecommerce.entity.User;
import com.mdtalalwasim.ecommerce.repository.OrderRepository;
import com.mdtalalwasim.ecommerce.repository.UserRepository;

@Service
public class OrderService {
    
    @Autowired
    OrderRepository orderRepository;
    
    @Autowired
    UserRepository userRepository;
    
    public Boolean saveOrder(Long userId, List<Cart> carts, Double totalPrice,
                            String address, String city, String state, String zipCode, String phoneNumber) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if(user == null) return false;
            
            Order order = new Order(user, totalPrice, address, city, state, zipCode, phoneNumber);
            orderRepository.save(order);
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }
    
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}

