package com.animal.mypet.payment;

import com.animal.mypet.item.Item;
import com.animal.mypet.order.OrderEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private int quantity;
    
    @ManyToOne
    private Item item; // 주문 품목
    
    @ManyToOne
    private OrderEntity order; // 주문과의 관계

    // Getters and Setters
}