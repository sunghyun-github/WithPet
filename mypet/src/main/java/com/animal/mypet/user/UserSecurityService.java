package com.animal.mypet.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserSecurityService implements UserDetailsService {

	  private final UserRepository userRepository;

	    @Override
	    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
	        Optional<User> _siteUser = userRepository.findByUserId(userId);
	        if (_siteUser.isEmpty()) {
	            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
	        }

	        User user = _siteUser.get();
	        List<GrantedAuthority> authorities = new ArrayList<>();

	        // 사용자의 역할 가져오기
	        String userRole = user.getUserRole();

	        // 역할에 따라 권한 부여
	        if (UserRole.ADMIN.getValue().equals(userRole)) {
	            authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue()));
	        } else if (UserRole.PETMANAGER.getValue().equals(userRole)) {
	            authorities.add(new SimpleGrantedAuthority(UserRole.PETMANAGER.getValue()));
	        } else {
	            authorities.add(new SimpleGrantedAuthority(UserRole.USER.getValue()));
	        }

	        return new org.springframework.security.core.userdetails.User(user.getUserId(), user.getUserPassword(), authorities);
	    }
}
