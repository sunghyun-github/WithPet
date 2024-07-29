package com.animal.mypet.cartitem;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.animal.mypet.item.Item;
import com.animal.mypet.item.ItemService;

@Service
public class CartService {

    @Autowired
    
    private CartItemRepository cartItemRepository;

    @Autowired
    private ItemService itemService;
// 장바구니 품목 추가
    public void addToCart(Long itemId, int quantity) {
        Optional<Item> optionalItem = itemService.getItemById(itemId);
        if (optionalItem.isPresent()) {
            Item item = optionalItem.get();
            List<CartItem> existingItems = cartItemRepository.findByItem(item);
            
            if (!existingItems.isEmpty()) {
                // 품목이 이미 장바구니에 존재하는 경우
                CartItem cartItem = existingItems.get(0);
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
                cartItemRepository.save(cartItem);
            } else {
                // 품목이 장바구니에 존재하지 않는 경우
                CartItem cartItem = new CartItem();
                cartItem.setItem(item);
                cartItem.setQuantity(quantity);
                cartItemRepository.save(cartItem);
            }
        }
    }
// 장바구니 품목 가져오기
    public List<CartItem> getAllCartItems() {
        return cartItemRepository.findAll();
    }
    
    // 장바구니에서 품목 삭제
    public void removeFromCart(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }
    
 // 장바구니의 총 가격 계산
    public double getTotalPrice() {
        List<CartItem> cartItems = getAllCartItems();
        return cartItems.stream()
                        .mapToDouble(cartItem -> cartItem.getQuantity() * cartItem.getItem().getPrice())
                        .sum();
    }
    
 // 장바구니 비우기
    public void clearCart() {
        cartItemRepository.deleteAll();
    }

}