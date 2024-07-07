package com.animal.mypet;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class indexController {
	
	@GetMapping("/")
	@ResponseBody
	public String index() {
		return "index 페이지 입니다.";
	}
}
