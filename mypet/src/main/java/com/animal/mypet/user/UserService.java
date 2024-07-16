package com.animal.mypet.user;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.animal.mypet.DataNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public void create(Map<String, String> userForm) throws Exception {
        Optional<User> existingUser = userRepository.findByUserId(userForm.get("userId"));
        if (existingUser.isPresent()) {
            throw new DataIntegrityViolationException("User already exists");
        }
        
        User user = new User();
        user.setUserId(userForm.get("userId"));
        user.setUserPassword(passwordEncoder.encode(userForm.get("userPassword")));
        user.setUserName(userForm.get("userName"));
        user.setUserPhone(userForm.get("userPhone"));
        user.setUserEmail(userForm.get("userEmail"));
        user.setUserRole(userForm.get("userRole"));
        
        userRepository.save(user);
    }
    
    // 역할 수정 메서드
    public void updateRole(String userId, String newRole) throws Exception {
        Optional<User> existingUser = userRepository.findByUserId(userId);
        if (existingUser.isEmpty()) {
            throw new Exception("User not found");
        }
        User user = existingUser.get();
        user.setUserRole(newRole);
        userRepository.save(user);
    }

    // 회원 탈퇴 메서드
    public void deleteUser(String userId) throws Exception {
        Optional<User> existingUser = userRepository.findByUserId(userId);
        if (existingUser.isEmpty()) {
            throw new Exception("User not found");
        }
        userRepository.delete(existingUser.get());
    }
    
    // 유저 @개 만드는거
    public List<User> createUsers(int numberOfUsers) {
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= numberOfUsers; i++) {
            User user = new User();
            user.setUserId("user" + i);
            user.setUserName("User " + i);
            user.setUserPhone("010-0000-00" + String.format("%02d", i));
            user.setUserEmail("user" + i + "@example.com");
            user.setUserRole("USER");
            user.setUserPassword("password" + i); // Set default password if needed
            users.add(saveUser(user));
        }
        return users;
    }

    private User saveUser(User user) {
        // Set creation and update timestamps using @PrePersist and @PreUpdate hooks
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    public User getUser(String username) {
        Optional<User> siteUser = this.userRepository.findByUserId(username);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("user not found");
        }
    }

}