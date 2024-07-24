package com.animal.mypet.board.file;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "BoardFile")
@Getter
@Setter
@Table(name = "board_file")
public class File {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "file_idx")
	private Integer fileIdx;

	@Column(name = "file_name", nullable = false)
	private String fileName; // 파일 이름

	@Column(name = "file_path", nullable = false)
	private String filePath; // 파일 저장 경로

	@Column(name = "file_type", nullable = false)
	private String fileType; // 파일 타입

}