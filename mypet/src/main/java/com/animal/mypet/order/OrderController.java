package com.animal.mypet.order;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OrderController {

    @GetMapping("/order/success")
    public String showOrderSuccessPage() {
        return "order/success"; // Thymeleaf 템플릿 이름
    }
}
