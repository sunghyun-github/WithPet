package com.animal.mypet.item.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.animal.mypet.item.Item;
import com.animal.mypet.item.ItemRepository;

@Service
public class FileService {

    private final Path rootLocation;
    private final FileRepository fileRepository;
    private final ItemRepository itemRepository;

    public FileService(@Value("${file.upload-dir}") String uploadDir, FileRepository fileRepository, ItemRepository itemRepository) {
        this.rootLocation = Paths.get(uploadDir);
        this.fileRepository = fileRepository;
        this.itemRepository = itemRepository;

        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    public File store(MultipartFile file, Item item) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store empty file");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }

        Path destinationFile = this.rootLocation.resolve(Paths.get(fileName)).normalize().toAbsolutePath();

        // Store the file on disk
        file.transferTo(destinationFile.toFile());

        // Create a File entity for metadata
        File fileEntity = new File();
        fileEntity.setFileName(fileName);
        fileEntity.setFilePath("/uploads/" + fileName); // Update this path based on your static resources
        fileEntity.setFileType(file.getContentType());
        fileEntity.setItem(item); // Associate the file with the item

        // Save file entity to the database
        fileRepository.save(fileEntity);

        return fileEntity;
    }
}
