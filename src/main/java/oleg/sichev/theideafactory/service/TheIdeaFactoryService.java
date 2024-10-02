package oleg.sichev.theideafactory.service;
import oleg.sichev.theideafactory.entity.*;
import oleg.sichev.theideafactory.repository.FileRepository;
import oleg.sichev.theideafactory.repository.NotificationRepository;
import oleg.sichev.theideafactory.repository.TheIdeaFactoryRepository;
import oleg.sichev.theideafactory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired TagService tagService;

    private static final String UPLOAD_DIRECTORY = "src/main/resources/uploads";

    public List<TheIdeaFactoryEntity> findAll() {
        return theIdeaFactoryRepository.findAll();
    }

    public TheIdeaFactoryEntity save(TheIdeaFactoryEntity theIdeaFactoryEntity) {
        Integer userId = getCurrentUserId();  // Получаем текущего userId
        if (userId != null) {
            theIdeaFactoryEntity.setUserId(userId);
        }
        return theIdeaFactoryRepository.save(theIdeaFactoryEntity);
    }

    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Optional<User> user = userRepository.findByUsername(userDetails.getUsername());
            return user.map(User::getId).orElse(null);
        }
        return null;
    }

    public TheIdeaFactoryEntity save(TheIdeaFactoryEntity theIdeaFactoryEntity, List<MultipartFile> files) throws IOException {
        TheIdeaFactoryEntity savedEntity = save(theIdeaFactoryEntity);  // Используем измененный метод save
        saveFiles(savedEntity, files);  // Сохраняем файлы после сохранения сущности
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
            TheIdeaFactoryEntity savedPost = theIdeaFactoryRepository.save(post);

            Long userId = post.getUserId().longValue(); // Конвертируем Integer в Long
            createNotification(userId, postId, "Вам ответили");

            return savedPost;
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

    public void approvePost(long postId) {
        TheIdeaFactoryEntity post = theIdeaFactoryRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post Id: " + postId));
        post.setApproved(true);
        post.setRejected(false); // Устанавливаем rejected в false
        theIdeaFactoryRepository.save(post);

        Long userId = post.getUserId().longValue(); // Конвертируем Integer в Long
        createNotification(userId, postId, "Ваша идея одобрена");
    }

    public void rejectPost(long postId) {
        TheIdeaFactoryEntity post = theIdeaFactoryRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post Id: " + postId));
        post.setApproved(false); // Устанавливаем approved в false
        post.setRejected(true);
        theIdeaFactoryRepository.save(post);

        Long userId = post.getUserId().longValue(); // Конвертируем Integer в Long
        createNotification(userId, postId, "Ваша идея отклонена");
    }

    public List<TheIdeaFactoryEntity> findApprovedPosts() {
        return theIdeaFactoryRepository.findByApproved(true);
    }

    public List<TheIdeaFactoryEntity> getDeletedPosts() {
        return theIdeaFactoryRepository.findByIsDeletedTrue();
    }

    private void createNotification(Long userId, Long postId, String message) {
        NotificationEntity notification = new NotificationEntity();
        notification.setUserId(userId);
        notification.setPostId(postId);
        notification.setMessage(message);
        notification.setRead(false);
        notificationRepository.save(notification);
    }

    public void createPostWithTags(Integer userId, String username, String message, List<String> tagNames, List<MultipartFile> files, Path uploadsDir, boolean anonymous) throws IOException {
        TheIdeaFactoryEntity post = new TheIdeaFactoryEntity();
        post.setUserId(userId);
        post.setUsername(username);
        post.setMessage(message);
        post.setApproved(false); // Установка значения по умолчанию
        post.setAnonymous(anonymous);

        // Добавляем теги к посту
        for (String tagName : tagNames) {
            Tag tag = tagService.findOrCreateTag(tagName.trim());
            post.addTag(tag); // Добавляем тег к посту
        }

        // Сохраняем пост до сохранения файлов
        theIdeaFactoryRepository.save(post);

        // Обработайте и сохраните файлы, если они есть
        if (files != null && !files.isEmpty()) {
            saveFiles(post, files); // Используйте метод saveFiles для загрузки файлов
        }
    }


    public void createPostWithTags(Integer userId, String username, String message, List<String> tagNames, boolean anonymous) {
        TheIdeaFactoryEntity post = new TheIdeaFactoryEntity();
        post.setUserId(userId);
        post.setUsername(username);
        post.setMessage(message);
        post.setApproved(false); // Установка значения по умолчанию
        post.setAnonymous(anonymous);

        // Добавляем теги к посту
        for (String tagName : tagNames) {
            Tag tag = tagService.findOrCreateTag(tagName.trim());
            post.addTag(tag); // Добавляем тег к посту
        }

        // Сохраняем сам пост
        theIdeaFactoryRepository.save(post);
    }


    public List<TheIdeaFactoryEntity> findAllPosts() {
        return theIdeaFactoryRepository.findAll();  // Убедитесь, что ваш репозиторий есть.
    }
}