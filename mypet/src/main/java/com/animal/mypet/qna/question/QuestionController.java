package com.animal.mypet.qna.question;

import java.security.Principal;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.animal.mypet.qna.answer.AnswerForm;
import com.animal.mypet.user.User;
import com.animal.mypet.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/qna_question")
@RequiredArgsConstructor
@Controller
public class QuestionController {

	private final QuestionService questionService;
	private final UserService userService;

	private String getCurrentUserId(Principal principal) {
		if (principal instanceof OAuth2AuthenticationToken) {
			OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) principal;
			OAuth2User oauth2User = oauth2Token.getPrincipal();
			Map<String, Object> attributes = oauth2User.getAttributes();
			String provider = oauth2Token.getAuthorizedClientRegistrationId();

			if ("kakao".equals(provider)) {
				return provider + "_" + attributes.get("id").toString();
			} else if ("naver".equals(provider)) {
				Map<String, Object> response = (Map<String, Object>) attributes.get("response");
				return provider + "_" + response.get("id").toString();
			}
		} else if (principal instanceof UsernamePasswordAuthenticationToken) {
			return principal.getName();
		}
		return null;
	}

	@GetMapping("/list")
	public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "kw", defaultValue = "") String kw,
			@RequestParam(value = "category", defaultValue = "") String category) {
		Page<Question> paging = this.questionService.getList(page, kw, category);
		model.addAttribute("paging", paging);
		model.addAttribute("category", category); // 카테고리 추가
		model.addAttribute("kw", kw);
		return "/qna/question_list";
	}

	@GetMapping("/detail/{question_idx}")
	public String detail(Model model, @PathVariable("question_idx") Integer questionIdx, AnswerForm answerForm, Principal principal) {
		Question question = this.questionService.getQuestion(questionIdx);
		model.addAttribute("question", question);

		String currentUserId = getCurrentUserId(principal);
		model.addAttribute("currentUserId", currentUserId);

		return "/qna/question_detail";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/create")
	public String create(QuestionForm questionForm) {
		return "/qna/question_form";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create")
	public String create(@Valid QuestionForm questionForm, BindingResult bindingResult,
	                     @RequestParam("files") MultipartFile[] files,
	                     Principal principal) {
	    if (bindingResult.hasErrors()) {
	        return "qna/question_form";
	    }

	    User user = null;
	    String currentUserId = getCurrentUserId(principal);

	    if (currentUserId != null) {
	        user = this.userService.getUser(currentUserId);
	    }

	    if (user == null) {
	        // 사용자 정보가 없는 경우 로그인 페이지로 리디렉션
	        return "redirect:/user/login";
	    }

	    // 게시글 생성 처리
	    this.questionService.create(questionForm.getCategory(), questionForm.getSubject(), questionForm.getContent(), files, user);

	    return "redirect:/qna_question/list";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{question_idx}")
	public String modify(QuestionForm questionForm, @PathVariable("question_idx") Integer questionIdx, Principal principal) {
		Question question = this.questionService.getQuestion(questionIdx);
		String currentUserId = getCurrentUserId(principal);

		if (!question.getAuthor().getUserId().equals(currentUserId)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
		}

		questionForm.setCategory(question.getCategory());
		questionForm.setSubject(question.getSubject());
		questionForm.setContent(question.getContent());
		return "/qna/question_form";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{question_idx}")
	public String modify(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal,
			@PathVariable("question_idx") Integer questionIdx,
			@RequestParam(value = "files", required = false) MultipartFile[] files) {
		if (bindingResult.hasErrors()) {
			return "/qna/question_form";
		}

		Question question = this.questionService.getQuestion(questionIdx);
		String currentUserId = getCurrentUserId(principal);

		if (!question.getAuthor().getUserId().equals(currentUserId)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
		}

		this.questionService.modify(question, questionForm.getCategory(), questionForm.getSubject(),
				questionForm.getContent(), files);
		return String.format("redirect:/qna_question/detail/%s", questionIdx);
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/delete/{question_idx}")
	public String questionDelete(Principal principal, @PathVariable("question_idx") Integer questionIdx) {
		Question question = this.questionService.getQuestion(questionIdx);
		String currentUserId = getCurrentUserId(principal);

		if (!question.getAuthor().getUserId().equals(currentUserId)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
		}
		this.questionService.delete(question);
		return "redirect:/";
	}
}
