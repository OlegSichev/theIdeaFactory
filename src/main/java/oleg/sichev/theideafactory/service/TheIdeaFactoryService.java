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
import java.util.Optional;

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

    public List<TheIdeaFactoryEntity> findByCategory(Long categoryId) {
        return theIdeaFactoryRepository.findByCategory_Id(categoryId);
    }

    public List<TheIdeaFactoryEntity> getActivePosts() {
        return theIdeaFactoryRepository.findByIsDeletedFalse();
    }

    public Optional<TheIdeaFactoryEntity> findById(Long id) {
        return theIdeaFactoryRepository.findById(id);
    }

    public TheIdeaFactoryEntity addAnswer(Long postId, String answer, String answeredBy) {
        Optional<TheIdeaFactoryEntity> postOptional = theIdeaFactoryRepository.findById(postId);
        if (postOptional.isPresent()) {
            TheIdeaFactoryEntity post = postOptional.get();
            String fullAnswer = answeredBy + ": " + answer;
            post.setAnswer(fullAnswer);  // Сохраняем ответ вместе с информацией о пользователе
            return theIdeaFactoryRepository.save(post);
        }
        throw new IllegalArgumentException("Post not found with ID: " + postId);
    }

    public void editPost(long postId, String username, String message) {
        TheIdeaFactoryEntity post = theIdeaFactoryRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + postId));
        post.setUsername(username);
        post.setMessage(message);
        theIdeaFactoryRepository.save(post);
    }

    public void deletePost(long postId) {
        theIdeaFactoryRepository.deleteById(postId);
    }

    public void approvePost(long postId, boolean approved) {
        TheIdeaFactoryEntity post = theIdeaFactoryRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + postId));
        post.setApproved(approved);
        theIdeaFactoryRepository.save(post);
    }

    public List<TheIdeaFactoryEntity> findApprovedPosts() {
        return theIdeaFactoryRepository.findByApproved(true);
    }

    public List<TheIdeaFactoryEntity> getDeletedPosts() {
        return theIdeaFactoryRepository.findByIsDeletedTrue();
    }

    public List<TheIdeaFactoryEntity> getAllEntriesWithComments() {
        return theIdeaFactoryRepository.findAllEntriesWithComments();
    }
}