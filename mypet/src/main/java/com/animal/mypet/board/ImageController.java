package com.animal.mypet.board;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@RequestMapping("/post")
@Controller
public class ImageController {
	
	@Autowired
    private final WebApplicationContext context;

    @PostMapping(value = "/image-upload")
    public ResponseEntity<?> imageUpload(@RequestParam("file") MultipartFile file) throws IllegalStateException, IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("파일이 비어 있습니다.");
        }

        try {
            // 서버에 저장할 경로
        	String uploadDirectory = new ClassPathResource("static/upload").getFile().getAbsolutePath();

            // 업로드 된 파일의 이름
            String originalFileName = file.getOriginalFilename();
            
            if (originalFileName == null || originalFileName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("파일 이름이 유효하지 않습니다.");
            }

            // 업로드 된 파일의 확장자
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));

            // 업로드 될 파일의 이름 재설정 (중복 방지를 위해 UUID 사용)
            String uuidFileName = UUID.randomUUID().toString() + fileExtension;

            // 위에서 설정한 서버 경로에 이미지 저장
            File destinationFile = new File(uploadDirectory, uuidFileName);
            file.transferTo(destinationFile);

            System.out.println("************************ 업로드 컨트롤러 실행 ************************");
            System.out.println(uploadDirectory);

            // Ajax에서 업로드 된 파일의 이름을 응답 받을 수 있도록 해줍니다.
            return ResponseEntity.ok(uuidFileName);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("이미지 업로드 실패");
        }
    }
}