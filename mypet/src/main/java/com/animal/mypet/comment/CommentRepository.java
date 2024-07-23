package com.animal.mypet.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.animal.mypet.board.Board;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
	Page<Comment> findAllByBoard(Board board, Pageable pageable);
}
