package com.animal.mypet.board.file;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service("boardFileService")
public class FileService {

    @Value("${file.upload-dir}")
    private String fileUploadDir;

    public String saveFile(MultipartFile multipartFile) throws IOException {
        String originalFileName = multipartFile.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String savedFileName = UUID.randomUUID() + extension;

        File targetFile = new File(fileUploadDir + savedFileName);
        InputStream fileStream = multipartFile.getInputStream();
        FileUtils.copyInputStreamToFile(fileStream, targetFile);

        return savedFileName;
    }

    public void deleteFile(String fileName) {
        File targetFile = new File(fileUploadDir + fileName);
        FileUtils.deleteQuietly(targetFile);
    }
}
