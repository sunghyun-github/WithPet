package com.animal.mypet;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.animal.mypet.user.UserService;

@SpringBootTest
class MypetApplicationTests {

	@Autowired
    private UserService userService;
	 @Test
	    void contextLoads() {
	    }
	
}
