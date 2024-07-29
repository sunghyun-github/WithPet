package com.animal.mypet.item;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.animal.mypet.cartitem.CartItem;
import com.animal.mypet.cartitem.CartService;
import com.animal.mypet.item.file.File;
import com.animal.mypet.item.file.FileService;
import com.animal.mypet.order.OrderEntity;
import com.animal.mypet.order.OrderService;

@Controller
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private CartService cartService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private FileService fileService;
    
// 품목 목록페이지
    @GetMapping("/items")
    public String showItemList(Model model) {
        model.addAttribute("items", itemService.getAllItems());
        return "item/item-list";
    }
// 품목 추가 페이지
    @GetMapping("/add-item")
    public String showAddItemPage(Model model) {
        model.addAttribute("item", new Item());
        return "item/add-item";
    }
// //품목 추가 처리
//    @PostMapping("/add-item")
//    public String addItem(@ModelAttribute Item item) {
//        itemService.saveItem(item);
//        return "redirect:/items";
//    }
    
//    @PostMapping("/add-item")
//    public String addItem(@ModelAttribute Item item, @RequestParam("file") MultipartFile file, Model model) {
//        try {
//            // 아이템 저장
//            itemService.saveItem(item);
//
//            // 파일 저장
//            if (!file.isEmpty()) {
//                File fileEntity = fileService.store(file, item);
//                // 파일 엔티티를 데이터베이스에 저장해야 할 경우
//                // fileRepository.save(fileEntity);
//            }
//
//            return "redirect:/items";
//        } catch (IOException e) {
//            e.printStackTrace();
//            model.addAttribute("error", "File upload failed: " + e.getMessage());
//            return "item/add-item";
//        }
//    }
    
    @PostMapping("/add-item")
    public String addItem(@RequestParam("name") String name,
                          @RequestParam("price") double price,
                          @RequestParam("file") MultipartFile file,
                          Model model) {
        try {
            Item item = new Item();
            item.setName(name);
            item.setPrice(price);
            itemService.saveItem(item);

            // Store the file
            if (!file.isEmpty()) {
                File fileEntity = fileService.store(file, item);
                item.setImageUrl(fileEntity.getFilePath()); // 이미지 URL 설정
                itemService.saveItem(item); // 아이템 업데이트
            }

            return "redirect:/items";
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "File upload failed: " + e.getMessage());
            return "item/add-item";
        }
    }
    
// 장바구니에 품목 추가
    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam("itemId") Long itemId, @RequestParam("quantity") int quantity) {
        cartService.addToCart(itemId, quantity);
        return "redirect:/items";
    }
// 장바구나 페이지
    @GetMapping("/cart")
    public String showCart(Model model) {
        model.addAttribute("cartItems", cartService.getAllCartItems());
        model.addAttribute("totalPrice", cartService.getTotalPrice());
        return "item/cart";
    }
    // 장바구니에서 품목 삭제
    @PostMapping("/remove-from-cart")
    public String removeFromCart(@RequestParam("cartItemId") Long cartItemId) {
        cartService.removeFromCart(cartItemId);
        return "redirect:/cart";
    }
 // 결제 페이지
    @PostMapping("/checkout")
    public String checkout(Model model) {
        List<CartItem> cartItems = cartService.getAllCartItems();
        double totalAmount = cartItems.stream()
                                       .mapToDouble(item -> item.getItem().getPrice() * item.getQuantity())
                                       .sum();
        OrderEntity order = orderService.createOrder(cartItems);
        model.addAttribute("order", order);
        model.addAttribute("totalAmount", totalAmount);
        return "item/checkout";  // 템플릿 경로를 정확히 지정
    }

}
