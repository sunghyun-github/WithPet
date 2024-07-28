package com.animal.mypet.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.animal.mypet.board.Board;
import com.animal.mypet.board.BoardService;
import com.animal.mypet.qna.question.Question;
import com.animal.mypet.qna.question.QuestionRepository;
import com.animal.mypet.qna.question.QuestionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final UserRepository userRepository;
	private final BoardService boardService;
	private final QuestionService questionService;

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

		String userEmail = userCreateForm.getEmailPrefix() + "@" + userCreateForm.getEmailDomain();

		Map<String, String> userForm = new HashMap<>();
		userForm.put("userId", userCreateForm.getUserId());
		userForm.put("userPassword", userCreateForm.getUserPassword());
		userForm.put("userName", userCreateForm.getUserName());
		userForm.put("userPhone", userCreateForm.getUserPhone());
		userForm.put("userEmail", userEmail);
		userForm.put("userRole", "USER");

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

	// ID 유효성검사
	@GetMapping("/validateId")
	public ResponseEntity<Map<String, Boolean>> validateUserId(@RequestParam("userId") String userId) {
		boolean isValid = userService.isUserIdAvailable(userId);
		Map<String, Boolean> response = new HashMap<>();
		response.put("valid", isValid);
		return ResponseEntity.ok(response);
	}

	// ID 찾기
	@GetMapping("/findId")
	public String showFindIdForm() {
		return "login/findId"; // 아이디 찾기 폼 페이지로 이동
	}

	@PostMapping("/findId")
	public String findId(@RequestParam("userName") String name, @RequestParam("userEmail") String email,
			@RequestParam("userPhone") String phone, Model model) {
		try {
			String userId = userService.findUserIdByNameEmailAndPhone(name, email, phone);
			model.addAttribute("userId", userId);
			return "login/idFound"; // 아이디 찾기 결과 페이지로 이동
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
			return "login/findId"; // 에러 발생 시 같은 페이지로 돌아감
		}
	}

	// PW 찾기ㅣ
	@GetMapping("/findPassword")
	public String showFindPasswordForm() {
		return "login/findPassword"; // 아이디 찾기 폼 페이지로 이동
	}

	@PostMapping("/findPassword")
	public String findPassword(@RequestParam("userId") String userId, @RequestParam("userName") String userName,
			@RequestParam("userPhone") String userPhone, Model model) {
		try {
			// 사용자 정보 확인
			User user = userService.getUser(userId);
			if (user.getUserName().equals(userName) && user.getUserPhone().equals(userPhone)) {
				// 사용자 ID를 뷰에 전달
				model.addAttribute("userId", userId);
				return "login/changePassword"; // 비밀번호 변경 페이지로 이동
			} else {
				throw new Exception("입력한 정보가 일치하지 않습니다.");
			}
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
			return "login/findPassword"; // 에러 발생 시 비밀번호 찾기 페이지로 돌아감
		}
	}

	// 비밀번호 변경 폼 페이지로 이동
	@GetMapping("/changePassword")
	public String showChangePasswordForm() {
		return "login/findPassword"; // 비밀번호 변경 폼 페이지로 이동
	}

	// 비밀번호 변경 요청을 처리합니다
	@PostMapping("/changePassword")
	public String changePassword(@RequestParam("userId") String userId, @RequestParam("newPassword") String newPassword,
			@RequestParam("confirmNewPassword") String confirmNewPassword, Model model) {
		// 비밀번호와 비밀번호 확인이 일치하는지 확인
		if (!newPassword.equals(confirmNewPassword)) {
			model.addAttribute("errorMessage", "새 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
			return "login/changePassword";
		}

		try {
			// 비밀번호 변경 요청 처리
			userService.changePassword(userId, newPassword);
			// 비밀번호 변경 성공 시 루트 페이지로 리다이렉션
			return "redirect:/";
		} catch (Exception e) {
			// 예외 발생 시 에러 메시지와 함께 원래 페이지로 돌아감
			model.addAttribute("errorMessage", e.getMessage());
			return "login/changePassword";
		}
	}

	// 회원 탈퇴 폼 페이지로 이동
	@GetMapping("/deleteAccount")
	public String showDeleteAccountForm() {
		return "login/deleteAccount"; // 템플릿 위치와 이름에 맞게 수정
	}

	// 회원탈퇴
	@PostMapping("/deleteAccount")
	public String deleteAccount(@RequestParam("confirmPassword") String confirmPassword, Model model,
			HttpServletRequest request, HttpServletResponse response) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userId = authentication.getName(); // 현재 로그인된 사용자 ID를 가져옴

		try {
			boolean isDeleted = userService.deleteAccount(userId, confirmPassword);
			if (isDeleted) {
				// 세션을 무효화하고 로그아웃 처리
				SecurityContextHolder.clearContext(); // 인증 정보를 지움
				request.getSession().invalidate(); // 세션 무효화
				response.addHeader("Set-Cookie", "JSESSIONID=; Path=/; HttpOnly; Max-Age=0"); // 세션 쿠키 삭제
				return "redirect:/"; // 루트 페이지로 리다이렉트
			} else {
				model.addAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
				return "login/deleteAccount"; // 에러 페이지로 이동
			}
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
			return "login/deleteAccount"; // 에러 발생 시 같은 페이지로 돌아감
		}
	}

//    마이페이지로 이동

	 @GetMapping("/mypage")
	    public String getUserMypage(Model model) {
	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        String userId = authentication.getName();

	        // 사용자 정보 가져오기
	        User user = userService.findByUserId(userId);
	        if (user == null) {
	            return "redirect:/error";
	        }

	        // 사용자가 작성한 게시물 가져오기
	        List<Board> userBoards = boardService.getBoardsByAuthor(userId);
	        List<Question> userQuestions = questionService.getQuestionsByAuthor(userId);

	        model.addAttribute("user", user);
	        model.addAttribute("userBoards", userBoards);
	        model.addAttribute("userQuestions", userQuestions);

	        return "mypage/form";
	    }

//    정보수정 페이지로 이동
	@GetMapping("/editProfile")
	public String editProfile(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userId = authentication.getName();

		User user = userService.findByUserId(userId);

		model.addAttribute("user", user);
		return "mypage/editForm";
	}

//    정보수정 처리
	@PostMapping("/editProfile")
	public String editProfile(@RequestParam("userId") String userId, @RequestParam("userName") String userName,
			@RequestParam("userEmail") String userEmail, @RequestParam("userPhone") String userPhone, Model model) {
		try {
			// 사용자 정보 업데이트
			userService.editProfile(userId, userName, userEmail, userPhone);
		} catch (Exception e) {
			// 예외가 발생하면 에러 메시지를 모델에 추가하고 폼 페이지로 리다이렉트
			model.addAttribute("errorMessage", e.getMessage());
			return "mypage/editForm";
		}
		// 업데이트 후 마이페이지로 리다이렉트
		return "redirect:/user/mypage";
	}
}