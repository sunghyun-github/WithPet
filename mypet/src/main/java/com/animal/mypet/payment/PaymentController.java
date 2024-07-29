package com.animal.mypet.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-payment-intent")
    public String createPaymentIntent(@RequestParam("amount") double amount) {
        try {
            String token = paymentService.getAccessToken();
            // 여기서 실제 결제 요청을 KG이니시스 API에 전달하거나, 아임포트 API를 통해 처리합니다.
            // 예를 들어, 결제 요청을 생성하거나, 결제 URL을 리턴할 수 있습니다.
            return "결제 요청 성공"; // 실제 결제 URL 등을 반환
        } catch (Exception e) {
            e.printStackTrace();
            return "결제 요청 실패";
        }
    }

    @PostMapping("/verify-payment")
    public RedirectView verifyPayment(@RequestParam("impUid") String impUid, @RequestParam("amount") double amount) {
        try {
            String token = paymentService.getAccessToken();
            boolean isVerified = paymentService.verifyPayment(token, impUid, amount);

            if (isVerified) {
                // 결제 검증이 성공하면 성공 페이지로 리디렉션
                return new RedirectView("/order/success");
            } else {
                // 결제 검증이 실패하면 실패 페이지로 리디렉션
                return new RedirectView("/order/failure");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 오류가 발생하면 실패 페이지로 리디렉션
            return new RedirectView("/order/failure");
        }
        
    }
    

}
