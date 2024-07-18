package com.animal.mypet.qna.answer;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.animal.mypet.DataNotFoundException;
import com.animal.mypet.qna.question.Question;
import com.animal.mypet.user.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AnswerService {
	 private final AnswerRepository answerRepository;

	 public void create(Question question, String content, User author) {
		    // 새로운 Answer 객체 생성
		    Answer answer = new Answer();
		    answer.setContent(content);
		    answer.setCreatedAt(LocalDateTime.now());
		    answer.setQuestion(question);
		    answer.setAuthor(author);
		    this.answerRepository.save(answer);
		}
	 
	 
	 public Answer getAnswer(Integer answerIdx) {
		 System.out.println(answerIdx);
	        Optional<Answer> answer = this.answerRepository.findById(answerIdx);
	        System.out.println(answerIdx);
	        if (answer.isPresent()) {
	            return answer.get();
	        } else {
	            throw new DataNotFoundException("answer not found");
	        }
	    }

	    public void modify(Answer answer, String content) {
	        answer.setContent(content);
	        this.answerRepository.save(answer);
	    }
	    
	    public void delete(Answer answer) {
	        this.answerRepository.delete(answer);
	    }
}
