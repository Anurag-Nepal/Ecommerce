package com.anuragNepal.ecommerce.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.anuragNepal.ecommerce.entity.Cart;
import com.anuragNepal.ecommerce.entity.User;
import com.anuragNepal.ecommerce.service.CartService;
import com.anuragNepal.ecommerce.service.PaymentService;
import com.anuragNepal.ecommerce.service.UserService;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final CartService cartService;
    private final UserService userService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public PaymentController(PaymentService paymentService, CartService cartService, UserService userService) {
        this.paymentService = paymentService;
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping("/create")
    public String createPayment(Principal principal, Model model) throws Exception {
        if (principal == null) {
            return "redirect:/signin";
        }
        User user = userService.getUserByEmail(principal.getName());
        List<Cart> carts = cartService.getCartsByUser(user.getId());
        if (carts.isEmpty()) {
            model.addAttribute("errorMsg", "Your cart is empty");
            return "redirect:/user/cart";
        }
        Double totalOrderPrice = carts.get(carts.size()-1).getTotalOrderPrice() + 250 + 100;
        long amountInMinorUnit = Math.round(totalOrderPrice * 100); // Stripe expects smallest unit (cents for USD)
        String checkoutUrl = paymentService.createCheckoutSession(amountInMinorUnit,
                baseUrl + "/payment/return",
                baseUrl + "/payment/cancel");
        return "redirect:" + checkoutUrl;
    }

    @GetMapping("/return")
    public String paymentReturn(Model model) {
        model.addAttribute("successMsg", "Payment successful via Stripe");
        return "message";
    }

    @GetMapping("/cancel")
    public String paymentCancel(Model model) {
        model.addAttribute("errorMsg", "Payment cancelled");
        return "message";
    }
}
