package com.animal.mypet.board;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.animal.mypet.comment.Comment;
import com.animal.mypet.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Board {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idx;
	
	@Column(length = 200)
	private String title;
	
	@Lob
	@Column(columnDefinition = "TEXT")
	private String content;
	
	@Column(name = "create_date")
	private LocalDateTime createDate;
	
	@Column(name = "modify_date")
	private LocalDateTime modifyDate;

	@OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE) 
	private List<Comment> commentList;
	
	@ManyToOne
	private User author;
	
	@Column(columnDefinition = "integer default 0", nullable = false)
	private int views;
	
	@ManyToMany
    private Set<User> voter;

}
