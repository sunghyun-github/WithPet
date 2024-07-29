package com.animal.mypet.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.animal.mypet.cartitem.CartItem;
import com.animal.mypet.cartitem.CartItemRepository;
import com.animal.mypet.cartitem.CartService;
import com.animal.mypet.item.Item;
import com.animal.mypet.payment.OrderItemRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private CartItemRepository cartItemRepository;

//    public OrderEntity createOrder() {
//        List<CartItem> cartItems = cartService.getAllCartItems();
//        if (cartItems.isEmpty()) {
//            throw new IllegalStateException("Cart is empty");
//        }
//        double totalPrice = cartItems.stream()
//                                      .mapToDouble(cartItem -> cartItem.getQuantity() * cartItem.getItem().getPrice())
//                                      .sum();
//
//        OrderEntity order = new OrderEntity();
//        order.setItems(cartItems);
//        order.setTotalPrice(totalPrice);
//
//        // 장바구니 비우기
//        cartService.clearCart();
//
//        return orderRepository.save(order);
//    }
    public OrderEntity createOrder(List<CartItem> cartItems) {
        OrderEntity order = new OrderEntity();
        double totalAmount = cartItems.stream()
                                      .mapToDouble(item -> item.getItem().getPrice() * item.getQuantity())
                                      .sum();
        order.setTotalPrice(totalAmount);
        order.setItems(cartItems);

        return orderRepository.save(order);
    }
}