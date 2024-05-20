package oleg.sichev.theideafactory.service;

import oleg.sichev.theideafactory.entity.File;
import oleg.sichev.theideafactory.entity.TheIdeaFactoryEntity;
import oleg.sichev.theideafactory.repository.FileRepository;
import oleg.sichev.theideafactory.repository.TheIdeaFactoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class TheIdeaFactoryService {
    @Autowired
    private TheIdeaFactoryRepository theIdeaFactoryRepository;

    @Autowired
    private FileRepository fileRepository;

    private static final String UPLOAD_DIRECTORY = "src/main/resources/uploads";

    public List<TheIdeaFactoryEntity> findAll() {
        return theIdeaFactoryRepository.findAll();
    }

    public TheIdeaFactoryEntity save(TheIdeaFactoryEntity theIdeaFactoryEntity) {
        return theIdeaFactoryRepository.save(theIdeaFactoryEntity);
    }

    public TheIdeaFactoryEntity save(TheIdeaFactoryEntity theIdeaFactoryEntity, List<MultipartFile> files) throws IOException {
        TheIdeaFactoryEntity savedEntity = save(theIdeaFactoryEntity);  // Используем оригинальный метод для сохранения поста
        saveFiles(savedEntity, files);     // Сохраняем файлы
        return savedEntity;
    }

    private void saveFiles(TheIdeaFactoryEntity theIdeaFactoryEntity, List<MultipartFile> files) throws IOException {
        // Создаем папку, если не существует
        Path uploadPath = Paths.get(UPLOAD_DIRECTORY);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Сохранение файлов
        for (MultipartFile fileUpload : files) {
            byte[] bytes = fileUpload.getBytes();
            Path path = Paths.get(uploadPath.toString(), fileUpload.getOriginalFilename());
            Files.write(path, bytes);

            File file = new File();
            file.setFileName(fileUpload.getOriginalFilename());
            file.setFilePath(path.toString());
            file.setEntry(theIdeaFactoryEntity);

            fileRepository.save(file);
        }
    }
}