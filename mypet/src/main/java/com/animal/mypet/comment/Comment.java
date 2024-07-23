package com.animal.mypet.comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.animal.mypet.board.Board;
import com.animal.mypet.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Comment {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idx;
	
	@Column(columnDefinition = "TEXT")
	private String content;
	
	@Column(name = "create_date")
	private LocalDateTime createDate;
	
	@Column(name = "modify_date")
	private LocalDateTime modifyDate;
	
	@ManyToOne
	private Board board;
	
	@ManyToOne
	private User author;
	
	@ManyToOne
    @JoinColumn(name = "parent_comment_id") // 새로 추가한 부분
    private Comment parentComment; // 부모 댓글

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.REMOVE)
    private List<Comment> commentList; // 자식 댓글 목록
    
    @ManyToMany
    private Set<User> voter;
}
