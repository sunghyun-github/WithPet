package com.animal.mypet.qna.answer;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import com.animal.mypet.qna.question.Question;
import com.animal.mypet.qna.question.QuestionService;
import com.animal.mypet.user.User;
import com.animal.mypet.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/qna_answer")
@Controller
public class AnswerController {

	private final QuestionService questionService;
	private final AnswerService answerService;
	private final UserService userService;

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create/{question_idx}")
	public String create(Model model, @PathVariable("question_idx") Integer questionIdx, @Valid AnswerForm answerForm,
			BindingResult bindingResult, Principal principal) {
		Question question = this.questionService.getQuestion(questionIdx);
		User user = this.userService.getUser(principal.getName());
		if (bindingResult.hasErrors()) {
			model.addAttribute("question", question);
			return "qna/question_detail";
		}
		this.answerService.create(question, answerForm.getContent(), user);
		return String.format("redirect:/qna_question/detail/%s", questionIdx);
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{answer_idx}")
	public String modify(@PathVariable("answer_idx") Integer answerIdx, Principal principal, Model model) {
		try {
			Answer answer = this.answerService.getAnswer(answerIdx);
			if (answer == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "답변을 찾을 수 없습니다.");
			}
			if (!answer.getAuthor().getUserId().equals(principal.getName())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
			}
			AnswerForm answerForm = new AnswerForm();
			answerForm.setContent(answer.getContent());
			model.addAttribute("answerForm", answerForm);
			return "/qna/answer_form";
		} catch (Exception e) {
			// 예외 로그를 남기고, 사용자에게 적절한 에러 메시지를 표시합니다.
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");
		}
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{answer_idx}")
	public String modify(@Valid AnswerForm answerForm, BindingResult bindingResult,
			@PathVariable("answer_idx") Integer answerIdx, Principal principal) {
		if (bindingResult.hasErrors()) {
			return "/qna/answer_form";
		}
		Answer answer = this.answerService.getAnswer(answerIdx);
		if (!answer.getAuthor().getUserId().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}
		this.answerService.modify(answer, answerForm.getContent());
		return String.format("redirect:/qna_question/detail/%s", answer.getQuestion().getQuestionIdx());
	}
	
	
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{answer_idx}")
    public String answerDelete(Principal principal, @PathVariable("answer_idx") Integer answerIdx) {
        Answer answer = this.answerService.getAnswer(answerIdx);
        if (!answer.getAuthor().getUserId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.answerService.delete(answer);
        return String.format("redirect:/qna_question/detail/%s", answer.getQuestion().getQuestionIdx());
    }
}