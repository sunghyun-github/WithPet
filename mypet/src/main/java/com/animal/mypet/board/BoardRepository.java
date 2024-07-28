package com.animal.mypet.board;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.animal.mypet.user.User;

public interface BoardRepository extends JpaRepository<Board, Integer> {
	Board findByTitle(String title);
	Board findByTitleAndContent(String title, String content);
    List<Board> findByTitleLike(String title);
    Page<Board> findAll(Pageable pageable);
    Page<Board> findAll(Specification<Board> spec, Pageable pageable);
    List<Board> findByAuthor(User author);
}
