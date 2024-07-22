package com.animal.mypet.qna.question;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.animal.mypet.DataNotFoundException;
import com.animal.mypet.qna.answer.Answer;
import com.animal.mypet.qna.file.File;
import com.animal.mypet.qna.file.FileService;
import com.animal.mypet.user.User;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class QuestionService {

	private final QuestionRepository questionRepository;
	private final FileService fileService;

	public List<Question> getList() {
		return this.questionRepository.findAll();
	}

	public Page<Question> getList(int page, String kw, String category) {
		// 정렬 기준을 저장할 리스트 생성
		List<Sort.Order> sorts = new ArrayList<>();
		// createdAt 필드를 기준으로 내림차순 정렬 추가
		sorts.add(Sort.Order.desc("createdAt"));
		// Pageable 객체를 생성, 페이지 번호와 페이지 크기, 정렬 기준 설정
		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
		Specification<Question> spec = search(kw, category); // search 메서드 수정

		// 페이징된 질문 목록을 조회하여 반환
		return this.questionRepository.findAll(spec, pageable);
	}

	public Question getQuestion(Integer questionIdx) {
		// 주어진 questionIdx로 데이터베이스에서 질문을 조회
		Optional<Question> question = this.questionRepository.findById(questionIdx);

		// 질문이 존재하는지 확인
		if (question.isPresent()) {
			// 질문이 존재하면 질문 객체를 반환
			return question.get();
		} else {
			// 질문이 존재하지 않으면 DataNotFoundException 예외를 던짐
			throw new DataNotFoundException("question not found");
		}
	}

	public Question create(String category, String subject, String content, MultipartFile[] files, User user) {
		// 새로운 Question 객체를 생성하고 제목, 내용, 작성 시간, 작성자를 설정
		Question q = new Question();
		q.setCategory(category);
		q.setSubject(subject);
		q.setContent(content);
		q.setCreatedAt(LocalDateTime.now());
		q.setAuthor(user);

		// 파일 엔티티 목록을 생성
		List<File> fileEntities = new ArrayList<>();
		if (files != null) { // 첨부 파일 배열이 null이 아닌 경우
			for (MultipartFile multipartFile : files) { // 각 파일을 순회
				if (!multipartFile.isEmpty()) { // 파일이 비어 있지 않으면
					try {
						// 파일을 저장하고 저장된 파일 엔티티를 가져옴
						File file = fileService.store(multipartFile);
						// 파일과 질문을 연관짓기 위해 파일 엔티티에 질문을 설정
						file.setQuestion(q);
						// 파일 엔티티를 파일 엔티티 목록에 추가
						fileEntities.add(file);
					} catch (IOException e) { // 파일 저장 중 예외가 발생한 경우
						throw new RuntimeException("File upload failed: " + e.getMessage());
					}
				}
			}
		}
		// 질문 객체에 파일 엔티티 목록을 설정
		q.setFiles(fileEntities);
		// 질문 객체를 데이터베이스에 저장하고 저장된 객체를 반환
		return questionRepository.save(q);
	}

	public void modify(Question question, String category, String subject, String content, MultipartFile[] files) {
		question.setCategory(category);
		question.setSubject(subject);
		question.setContent(content);
		question.setUpdatedAt(LocalDateTime.now());

		// 기존 파일 엔티티를 제거
		question.getFiles().clear();

		// 새로운 파일 엔티티를 추가
		if (files != null) {
			for (MultipartFile multipartFile : files) {
				if (!multipartFile.isEmpty()) {
					try {
						File file = fileService.store(multipartFile);
						file.setQuestion(question); // 파일과 질문을 연관짓기
						question.getFiles().add(file); // 파일 엔티티를 질문의 파일 목록에 추가
					} catch (IOException e) {
						throw new RuntimeException("File upload failed: " + e.getMessage());
					}
				}
			}
		}

		this.questionRepository.save(question);
	}

	public void delete(Question question) {
		this.questionRepository.delete(question);
	}

	private Specification<Question> search(String kw, String category) {
		return new Specification<Question>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
				query.distinct(true);
				Join<Question, User> u1 = q.join("author", JoinType.LEFT);
				Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);

				List<Predicate> predicates = new ArrayList<>();
				predicates.add(
						cb.or(cb.like(q.get("subject"), "%" + kw + "%"), cb.like(u1.get("userId"), "%" + kw + "%")));

				if (!category.isEmpty()) {
					predicates.add(cb.equal(q.get("category"), category));
				}

				return cb.and(predicates.toArray(new Predicate[0]));
			}
		};
	}
}
