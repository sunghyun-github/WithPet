package com.animal.mypet.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUserId(String userId);

	Optional<User> findByUserNameAndUserEmailAndUserPhone(String userName, String userEmail, String userPhone);
	
	// 비밀번호 초기화
	Optional<User> findByUserIdAndUserNameAndUserPhone(String userId, String userName, String userPhone);

    boolean existsByUserId(String userId);
    
    
    Optional<User> findByUserEmailAndProvider(String email, String provider);
    
}
