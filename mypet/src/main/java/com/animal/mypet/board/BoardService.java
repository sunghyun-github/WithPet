package com.animal.mypet.board;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.animal.mypet.DataNotFoundException;
import com.animal.mypet.comment.Comment;
import com.animal.mypet.user.User;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

import org.springframework.data.jpa.domain.Specification;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BoardService {
	
	private final BoardRepository boardRepository;
	
	 private Specification<Board> search(String kw) {
	        return new Specification<>() {
	            private static final long serialVersionUID = 1L;
	            @Override
	            public Predicate toPredicate(Root<Board> b, CriteriaQuery<?> query, CriteriaBuilder cb) {
	                query.distinct(true);  // 중복을 제거 
	                Join<Board, User> u1 = b.join("author", JoinType.LEFT);
	                Join<Board, Comment> a = b.join("commentList", JoinType.LEFT);
	                Join<Comment, User> u2 = a.join("author", JoinType.LEFT);
	                return cb.or(cb.like(b.get("title"), "%" + kw + "%"), // 제목 
	                        cb.like(b.get("content"), "%" + kw + "%"),      // 내용 
	                        cb.like(u1.get("userId"), "%" + kw + "%"),    // 질문 작성자 
	                        cb.like(a.get("content"), "%" + kw + "%"),      // 답변 내용 
	                        cb.like(u2.get("userId"), "%" + kw + "%"));   // 답변 작성자 
	            }
	        };
	    }
	
	public List<Board> getList() {
		return this.boardRepository.findAll();
	}
	
	public Board getBoard(Integer idx) {  
        Optional<Board> board = this.boardRepository.findById(idx);
        if (board.isPresent()) {
        	Board board1 = board.get();
        	board1.setViews(board1.getViews()+1);
        	this.boardRepository.save(board1);
            return board1;
        } else {
            throw new DataNotFoundException("게시글이 없습니다.");
        }
    }
	
	public List<Board> getTopBoards() {
		try {
            return boardRepository.findAll().stream()
                    .sorted((b1, b2) -> {
                        int score1 = b1.getViews() + b1.getCommentList().size() + b1.getVoter().size();
                        int score2 = b2.getViews() + b2.getCommentList().size() + b2.getVoter().size();
                        return Integer.compare(score2, score1);
                    })
                    .limit(5)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new DataNotFoundException("게시글 목록을 가져올 수 없습니다.");
        }
	}
	
	public Page<Board> getList(int page, String kw) {
		try {
            List<Sort.Order> sorts = new ArrayList<>();
            sorts.add(Sort.Order.desc("createDate"));
            Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
            Specification<Board> spec = search(kw);
            return this.boardRepository.findAll(spec, pageable);
        } catch (Exception e) {
            throw new DataNotFoundException("게시글 목록을 가져올 수 없습니다.");
        }
    }
	
	public void write(String title, String content, User user) {
        Board board = new Board();
        board.setTitle(title);
        board.setContent(content);
        board.setCreateDate(LocalDateTime.now());
        board.setAuthor(user);
        this.boardRepository.save(board);
    }
	
	public void modify(Board board, String title, String content) {
		board.setTitle(title);
		board.setContent(content);
		board.setModifyDate(LocalDateTime.now());
        this.boardRepository.save(board);
    }
	
	public void delete(Board board) {
        this.boardRepository.delete(board);
    }
	
	public void vote(Board board, User user) {
		board.getVoter().add(user);
        this.boardRepository.save(board);
    }
	
    
    @Transactional
    public void cancelVote(Board board, User user) {
    	board.getVoter().remove(user);
        this.boardRepository.save(board);
    }
	
}
