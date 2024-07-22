package com.animal.mypet.qna.question;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
	public String detail(Model model, @PathVariable("question_idx") Integer questionIdx, AnswerForm answerForm) {
		Question question = this.questionService.getQuestion(questionIdx);
		model.addAttribute("question", question);
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
			@RequestParam("files") MultipartFile[] files, Principal principal) {
		if (bindingResult.hasErrors()) {
			return "qna/question_form";
		}
		User user = this.userService.getUser(principal.getName());
		this.questionService.create(questionForm.getCategory(), questionForm.getSubject(), questionForm.getContent(),
				files, user);
		return "redirect:/qna_question/list";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{question_idx}")
	public String modify(QuestionForm questionForm, @PathVariable("question_idx") Integer questionIdx,
			Principal principal) {
		Question question = this.questionService.getQuestion(questionIdx);
		if (!question.getAuthor().getUserId().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
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
		if (!question.getAuthor().getUserId().equals(principal.getName())) {
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
		if (!question.getAuthor().getUserId().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
		}
		this.questionService.delete(question);
		return "redirect:/";
	}
}
