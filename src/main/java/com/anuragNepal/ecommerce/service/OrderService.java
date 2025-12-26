package com.anuragNepal.ecommerce.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.anuragNepal.ecommerce.entity.Cart;
import com.anuragNepal.ecommerce.entity.Order;
import com.anuragNepal.ecommerce.entity.User;
import com.anuragNepal.ecommerce.repository.OrderRepository;
import com.anuragNepal.ecommerce.repository.UserRepository;

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
    
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }
    
    public Boolean updateOrderStatus(Long orderId, String status) {
        try {
            Order order = orderRepository.findById(orderId).orElse(null);
            if(order == null) return false;
            
            order.setOrderStatus(status);
            orderRepository.save(order);
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

