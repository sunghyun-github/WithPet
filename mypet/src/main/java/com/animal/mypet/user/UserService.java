package com.animal.mypet.user;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.javamail.JavaMailSender;
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
        user.setUserRole("USER");
        
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
    
    // id 유효성검사
    public boolean isUserIdAvailable(String userId) {
        Optional<User> existingUser = userRepository.findByUserId(userId);
        return existingUser.isEmpty();
    }
    
    // 사용자 이름과 이메일로 ID 찾기
    public String findUserIdByNameEmailAndPhone(String name, String email, String phone) throws Exception {
        Optional<User> user = userRepository.findByUserNameAndUserEmailAndUserPhone(name, email, phone);
        if (user.isPresent()) {
            return user.get().getUserId();
        } else {
            throw new Exception("해당 사용자 정보를 찾을 수 없습니다.");
        }
    }
    
    // 비밀번호 초기화 메서드
    public String resetPassword(String userId, String name, String phone) throws Exception {
        Optional<User> userOpt = userRepository.findByUserIdAndUserNameAndUserPhone(userId, name, phone);
        if (userOpt.isEmpty()) {
            throw new Exception("해당 사용자 정보를 찾을 수 없습니다.");
        }

        User user = userOpt.get();
        String newPassword = generateRandomPassword();
        user.setUserPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return newPassword;
    }

    // 랜덤으로 비밀번호 바꿔줌
    private String generateRandomPassword() {
        // 랜덤 비밀번호 생성 로직 (예: 8자 길이의 랜덤 문자열)
        int length = 8;
        StringBuilder sb = new StringBuilder();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }
    
        
    // 비밀번호 변경
    public void changePassword(String userId, String currentPassword, String newPassword) throws Exception {
        Optional<User> userOpt = userRepository.findByUserId(userId);
        if (userOpt.isEmpty()) {
            throw new Exception("사용자를 찾을 수 없습니다.");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(currentPassword, user.getUserPassword())) {
            throw new Exception("현재 비밀번호가 맞지 않습니다.");
        }

        user.setUserPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    // 회원 탈퇴 메서드
    public boolean deleteAccount(String userId, String confirmPassword) throws Exception {
        Optional<User> userOpt = userRepository.findByUserId(userId);
        if (userOpt.isEmpty()) {
            throw new Exception("사용자를 찾을 수 없습니다.");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(confirmPassword, user.getUserPassword())) {
            return false; // 비밀번호 불일치
        }

        userRepository.delete(user);
        return true; // 성공적으로 삭제됨
    }
   
}
