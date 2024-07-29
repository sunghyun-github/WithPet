package com.animal.mypet.cartitem;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.animal.mypet.item.Item;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByItem(Item item);
}