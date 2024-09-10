package oleg.sichev.theideafactory.controller;

import jakarta.validation.Valid;
import oleg.sichev.theideafactory.entity.TheIdeaFactoryEntity;
import oleg.sichev.theideafactory.repository.TheIdeaFactoryRepository;
import oleg.sichev.theideafactory.service.LikeService;
import oleg.sichev.theideafactory.service.TheIdeaFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class TheIdeaFactoryAdminController {

    @Autowired
    private TheIdeaFactoryService theIdeaFactoryService;

    @Autowired
    TheIdeaFactoryRepository theIdeaFactoryRepository;

    @Autowired
    private LikeService likeService;


    @GetMapping("/theIdeaFactoryIndexAdmin")
    public String showAdminGuestBook(Model model) {
        List<TheIdeaFactoryEntity> entries = theIdeaFactoryService.findAll();
        model.addAttribute("entries", entries);
        model.addAttribute("entry", new TheIdeaFactoryEntity());
        return "theIdeaFactoryIndexAdmin";
    }

    @PostMapping("/theIdeaFactoryIndexAdmin")
    public String addAdminEntry(@Valid TheIdeaFactoryEntity theIdeaFactoryEntity,
                                @RequestParam("fileUpload") List<MultipartFile> files,
                                @RequestParam("anonymous") boolean anonymous, // Принимаем значение анонимус
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        if (authentication != null && authentication.isAuthenticated()) {
            theIdeaFactoryEntity.setUsername(authentication.getName()); // устанавливаем имя пользователя из аутентификации
        }

        theIdeaFactoryEntity.setAnonymous(anonymous); // Устанавливаем значение анонимус в сущность

        try {
            boolean hasFiles = false;

            // Проверка на наличие файлов и их содержимого
            if (files != null && !files.isEmpty()) {
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        hasFiles = true;
                        break;
                    }
                }
            }

            if (hasFiles) {
                // Создание директории, если она не существует
                Path uploadsDir = Paths.get("src", "main", "resources", "uploads");
                if (!Files.exists(uploadsDir)) {
                    Files.createDirectories(uploadsDir);
                }
                // Проверка прав доступа к директории
                if (!Files.isWritable(uploadsDir)) {
                    // Обработайте ошибку: у вас нет прав на запись в эту папку.
                    throw new IOException("Нет прав на запись в директорию 'uploads'");
                }
                // Сохранение поста и файлов
                theIdeaFactoryService.save(theIdeaFactoryEntity, files);
                redirectAttributes.addFlashAttribute("message", "Пост и файлы успешно загружены!");
            } else {
                // Сохранение только поста
                theIdeaFactoryService.save(theIdeaFactoryEntity); // Используем метод save без файлов
                redirectAttributes.addFlashAttribute("message", "Пост успешно загружен!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Ошибка при загрузке поста или файлов.");
        }
        return "redirect:/admin/theIdeaFactoryIndexAdmin";
    }


    @GetMapping("/getEntriesFromDatabase")
    @ResponseBody
    public List<TheIdeaFactoryEntity> getEntriesFromDatabaseAdmin() {
        return theIdeaFactoryService.findAll();
    }

    @GetMapping("/username")
    @ResponseBody
    public String currentAdminUserName(Authentication authentication) {
        return authentication != null ? authentication.getName() : "";
    }

    @GetMapping("/answer")
    public String showAnswerPage(@RequestParam Long id, Model model, Authentication authentication) {
        Optional<TheIdeaFactoryEntity> post = theIdeaFactoryService.findById(id);
        if (post.isPresent()) {
            model.addAttribute("post", post.get());
            if (authentication != null && authentication.isAuthenticated()) {
                model.addAttribute("authenticatedUser", authentication.getName());
            }
            return "answer"; // answer.html
        }
        return "redirect:/admin/theIdeaFactoryIndexAdmin"; // Пост не найден, редирект на главную страницу
    }

    @PostMapping("/answer")
    public String addAnswer(@RequestParam Long postId, @RequestParam String answer, @RequestParam String answeredBy, RedirectAttributes redirectAttributes) {
        try {
            theIdeaFactoryService.addAnswer(postId, answer, answeredBy);
            redirectAttributes.addFlashAttribute("successMessage", "Ответ успешно добавлен!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при добавлении ответа.");
        }
        return "redirect:/admin/theIdeaFactoryIndexAdmin";
    }

    @PostMapping("/editPost")
    public String editPost(@RequestParam long postId, @RequestParam String username, @RequestParam String message, RedirectAttributes redirectAttributes) {
        try {
            theIdeaFactoryService.editPost(postId, username, message);
            redirectAttributes.addFlashAttribute("successMessage", "Пост успешно обновлен!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при редактировании поста.");
        }
        return "redirect:/admin/theIdeaFactoryIndexAdmin";
    }

    @PostMapping("/deletePost")
    public String deletePost(@RequestParam long postId, RedirectAttributes redirectAttributes) {
        Optional<TheIdeaFactoryEntity> postOpt = theIdeaFactoryRepository.findById(postId);

        if (postOpt.isPresent()) {
            try {
                TheIdeaFactoryEntity post = postOpt.get();
                post.setDeleted(true); // Устанавливаем deleted в true
                theIdeaFactoryRepository.save(post); // Сохраняем изменения в базе данных
                redirectAttributes.addFlashAttribute("successMessage", "Пост успешно помечен как удалённый!");
            } catch (Exception e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при пометке поста как удалённого.");
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Пост не найден.");
        }
        return "redirect:/admin/theIdeaFactoryIndexAdmin";
    }



    @PostMapping("/approvePost")
    public String approvePost(@RequestParam long postId, RedirectAttributes redirectAttributes) {
        try {
            theIdeaFactoryService.approvePost(postId); // Отправляем только postId в сервис
            redirectAttributes.addFlashAttribute("successMessage", "Пост успешно обновлен!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении поста.");
        }
        return "redirect:/admin/theIdeaFactoryIndexAdmin";
    }

    @PostMapping("/rejectPost")
    public String rejectPost(@RequestParam long postId, RedirectAttributes redirectAttributes) {
        try {
            theIdeaFactoryService.rejectPost(postId); // Отправляем только postId в сервис
            redirectAttributes.addFlashAttribute("successMessage", "Пост успешно обновлен!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении поста.");
        }
        return "redirect:/admin/theIdeaFactoryIndexAdmin";
    }

    @GetMapping("/news")
    public String getApprovedPosts(Model model) {
        List<TheIdeaFactoryEntity> approvedPosts = theIdeaFactoryService.findApprovedPosts();
        model.addAttribute("posts", approvedPosts);
        return "news";
    }

    @PostMapping("/{entryId}/like")
    public ResponseEntity<String> likePost(@PathVariable long entryId, @RequestParam Integer userId) {
        if (likeService.likePost(entryId, userId)) {
            return ResponseEntity.ok("Post liked");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User has already liked this post");
        }
    }

    @PostMapping("/{id}/comment")
    public void commentEntry(@PathVariable Long id, @RequestBody String comment) {
        Optional<TheIdeaFactoryEntity> entityOptional = theIdeaFactoryRepository.findById(id);
        if (entityOptional.isPresent()) {
            TheIdeaFactoryEntity entity = entityOptional.get();
            entity.getComments().add(comment);
            theIdeaFactoryRepository.save(entity);
        }
    }

    @GetMapping("/tagReference")
    public String getTagsPage(){
        return "tagReference";
    }
}