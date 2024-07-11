package com.animal.mypet.user;

import java.util.Map;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
}
