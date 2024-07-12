package com.animal.mypet.user;

import java.util.HashMap;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final UserRepository userRepository; // UserRepository 추가
    
//    카카오톡 로그인
   @Value("${kakao.client_id}")
    private String client_id;

    @Value("${kakao.redirect_uri}")
    private String redirect_uri;
    
    
    
    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup/form";
    }
    
    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup/form";
        }
        
        if (!userCreateForm.getUserPassword().equals(userCreateForm.getUserPassword2())) {
            bindingResult.rejectValue("userPassword2", "passwordIncorrect", "패스워드가 다릅니다.");
            return "signup/form";
        }
        
        // 이메일 중복 검사
        Optional<User> existingUser = userRepository.findByUserEmail(userCreateForm.getUserEmail());
        if (existingUser.isPresent()) {
            bindingResult.rejectValue("userEmail", "emailDuplicate", "이미 등록된 이메일입니다.");
            return "signup/form";
        }
        
        Map<String, String> userForm = new HashMap<>();
        userForm.put("userId", userCreateForm.getUserId());
        userForm.put("userPassword", userCreateForm.getUserPassword());
        userForm.put("userName", userCreateForm.getUserName());
        userForm.put("userPhone", userCreateForm.getUserPhone());
        userForm.put("userEmail", userCreateForm.getUserEmail());
        userForm.put("userRole", userCreateForm.getUserRole());
        
        try {
            userService.create(userForm);    
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자 입니다.");
            return "signup/form";
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup/form";
        }
        
        return "redirect:/";
    }
    
    
    
    @GetMapping("/login")
    public String login(Model model) {
        String location = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="+client_id+"&redirect_uri="+redirect_uri;
         model.addAttribute("location", location);
        return "login/form";
    }

}