package com.animal.mypet.qna.question;


import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionForm {
	@NotEmpty(message="카테고리를 선택해주세요.")
    private String category;
	
    @NotEmpty(message="제목은 필수항목입니다.")
    @Size(max=200)
    private String subject;

    @NotEmpty(message="내용을 입력하세요.")
    private String content;
    
    private MultipartFile[] file;
   
}
