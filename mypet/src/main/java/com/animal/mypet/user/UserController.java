package com.animal.mypet.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final UserRepository userRepository; // UserRepository 추가
    
    
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
        return "login/form";
    }
    
    
 // 관리자 페이지 이동
    @GetMapping("/admin")
    public String adminPage(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin/adminMain";
    }

    @PostMapping("/editRole/{userId}")
    public String editUserRole(@PathVariable("userId") String userId, @RequestParam("newRole") String newRole) {
        Optional<User> userOpt = userRepository.findByUserId(userId);
        if (userOpt.isEmpty()) {
            return "redirect:/user/admin";
        }
        User user = userOpt.get();
        user.setUserRole(newRole);
        userRepository.save(user);
        return "redirect:/user/admin";
    }

    @PostMapping("/delete/{userId}")
    public String deleteUser(@PathVariable("userId") String userId) {
        Optional<User> userOpt = userRepository.findByUserId(userId);
        if (userOpt.isEmpty()) {
            return "redirect:/user/admin";
        }
        User user = userOpt.get();
        userRepository.delete(user);
        return "redirect:/user/admin";
    }

}