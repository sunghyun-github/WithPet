package com.animal.mypet.comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.animal.mypet.DataNotFoundException;
import com.animal.mypet.board.Board;
import com.animal.mypet.user.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommentService {
	
	private final CommentRepository commentRepository;
	
	public Comment create(Board board, String content, User user) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setCreateDate(LocalDateTime.now());
        comment.setAuthor(user);
        comment.setBoard(board);
        this.commentRepository.save(comment);
        return comment;
    }
	
	public Comment getComment(Integer idx) {
        Optional<Comment> answer = this.commentRepository.findById(idx);
        if (answer.isPresent()) {
            return answer.get();
        } else {
            throw new DataNotFoundException("comment not found");
        }
    }
	
	public Page<Comment> getList(Board board, int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page,20, Sort.by(sorts)); // 5개씩 끊어서 처리
        return this.commentRepository.findAllByBoard(board, pageable);
    }

    public void modify(Comment comment, String content) {
    	comment.setContent(content);
    	comment.setModifyDate(LocalDateTime.now());
        this.commentRepository.save(comment);
    }
	
    public void delete(Comment comment) {
        this.commentRepository.delete(comment);
    }
	
    public void vote(Comment comment, User user) {
    	comment.getVoter().add(user);
        this.commentRepository.save(comment);
    }
    
    @Transactional
    public void cancelVote(Comment comment, User user) {
    	comment.getVoter().remove(user);
        this.commentRepository.save(comment);
    }
    
    
}
