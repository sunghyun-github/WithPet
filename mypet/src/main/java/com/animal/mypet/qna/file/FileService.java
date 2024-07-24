package com.animal.mypet.qna.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Service("qnaFileService")
@Getter
@Setter
public class FileService {

	private final Path rootLocation;

	public FileService(@Value("${file.upload-dir}") String uploadDir) {
		this.rootLocation = Paths.get(uploadDir);
		// Ensure the directory exists
		try {
			Files.createDirectories(rootLocation);
		} catch (IOException e) {
			throw new RuntimeException("Could not create upload directory", e);
		}
	}

	public File store(MultipartFile file) throws IOException {
		// Ensure file is not empty
		if (file.isEmpty()) {
			throw new IllegalArgumentException("Cannot store empty file");
		}

		String fileName = file.getOriginalFilename();
		if (fileName == null || fileName.isEmpty()) {
			throw new IllegalArgumentException("File name cannot be null or empty");
		}

		// Resolve and normalize the file path
		Path destinationFile = this.rootLocation.resolve(Paths.get(fileName)).normalize().toAbsolutePath();

		// Store the file on disk
		file.transferTo(destinationFile.toFile());

		// Create a File entity for metadata
		File fileEntity = new File();
		fileEntity.setFileName(fileName);
		// Set the file path relative to the static/uploads directory
		fileEntity.setFilePath("/uploads/" + fileName);
		fileEntity.setFileType(file.getContentType());

		// Note: The fileEntity should be saved to the database in a repository method,
		// not here.
		return fileEntity;
	}
}