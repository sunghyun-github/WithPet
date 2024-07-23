package com.animal.mypet.email;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@Controller
@RequestMapping("/mail")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @ResponseBody
    @PostMapping("/isAuthenticated")
    public String MailSend(@RequestBody Map<String, String> request) {
        String mail = request.get("mail");
        System.out.println("받은 메일주소: " + mail);

        int number = mailService.sendMail(mail);
        String num = "" + number;

        return num;
    }
    
}
