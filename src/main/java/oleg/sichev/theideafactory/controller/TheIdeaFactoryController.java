package oleg.sichev.theideafactory.controller;

import oleg.sichev.theideafactory.entity.TheIdeaFactoryEntity;
import oleg.sichev.theideafactory.repository.TheIdeaFactoryRepository;
import oleg.sichev.theideafactory.service.LikeService;
import oleg.sichev.theideafactory.service.TheIdeaFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Controller
public class TheIdeaFactoryController {


    @Autowired
    private TheIdeaFactoryService theIdeaFactoryService;

    @Autowired
    private TheIdeaFactoryRepository theIdeaFactoryRepository;

    @Autowired
    private LikeService likeService;

    @GetMapping("/theIdeaFactoryIndex")
    public String showGuestBook(Model model, Authentication authentication) {
        List<TheIdeaFactoryEntity> entries = theIdeaFactoryService.findAll();
        model.addAttribute("entries", entries);
        model.addAttribute("entry", new TheIdeaFactoryEntity());

        // Проверка роли пользователя
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            boolean isAdmin = authorities.stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
            model.addAttribute("isAdmin", isAdmin);
        }

        return "theIdeaFactoryIndex";
    }



//    @GetMapping("/theIdeaFactoryIndex")
//    public String showGuestBook(Model model) {
//        List<TheIdeaFactoryEntity> entries = theIdeaFactoryService.findAll();
//        model.addAttribute("entries", entries);
//        model.addAttribute("entry", new TheIdeaFactoryEntity());
//        return "theIdeaFactoryIndex";
//    }

    @PostMapping("/theIdeaFactoryIndex")
    public String addEntry(TheIdeaFactoryEntity theIdeaFactoryEntity,
                           @RequestParam("fileUpload") List<MultipartFile> files,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        if (authentication != null && authentication.isAuthenticated()) {
            theIdeaFactoryEntity.setUsername(authentication.getName()); // устанавливаем имя пользователя из аутентификации
        }

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
        return "redirect:/theIdeaFactoryIndex";
    }

    @GetMapping("/getEntriesFromDatabase")
    @ResponseBody
    public List<TheIdeaFactoryEntity> getEntriesFromDatabase() {
        return theIdeaFactoryService.findAll();
    }

    @GetMapping("/username")
    @ResponseBody
    public String currentUserName(Authentication authentication) {
        return authentication != null ? authentication.getName() : "";
    }


    @GetMapping("/editIdea")
    public String showAnswerPage(@RequestParam Long id, Model model, Authentication authentication) {
        Optional<TheIdeaFactoryEntity> post = theIdeaFactoryService.findById(id);
        if (post.isPresent()) {
            model.addAttribute("post", post.get());
            if (authentication != null && authentication.isAuthenticated()) {
                model.addAttribute("authenticatedUser", authentication.getName());
            }
            return "editIdea"; // editIdea.html
        }
        return "redirect:/theIdeaFactoryIndex"; // Пост не найден, редирект на главную страницу
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
        return "redirect:/theIdeaFactoryIndex";
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
        return "redirect:/theIdeaFactoryIndex";
    }

    @GetMapping("/deleted")
    public List<TheIdeaFactoryEntity> getDeletedPosts() {
        return theIdeaFactoryService.getDeletedPosts();
    }

    @GetMapping("/news")
    public String getApprovedPosts(Model model, Authentication authentication) {
        List<TheIdeaFactoryEntity> approvedPosts = theIdeaFactoryService.findApprovedPosts();
        model.addAttribute("posts", approvedPosts);

        // Проверка роли пользователя
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            boolean isAdmin = authorities.stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
            model.addAttribute("isAdmin", isAdmin);
        }

        return "news";
    }

    @GetMapping("/ideasForModeration")
    public String getNotApprovedPosts(Model model, Authentication authentication) {
        List<TheIdeaFactoryEntity> approvedPosts = theIdeaFactoryService.findApprovedPosts();
        model.addAttribute("posts", approvedPosts);

        // Проверка роли пользователя
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            boolean isAdmin = authorities.stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
            model.addAttribute("isAdmin", isAdmin);
        }

        return "ideasForModeration";
    }
//    @GetMapping("/news")
//    public String getApprovedPosts(Model model) {
//        List<TheIdeaFactoryEntity> approvedPosts = theIdeaFactoryService.findApprovedPosts();
//        model.addAttribute("posts", approvedPosts);
//        return "news";
//    }

    @PostMapping("/{entryId}/like")
    public ResponseEntity<String> likePost(@PathVariable long entryId, @RequestParam Integer userId) {
        if (likeService.likePost(entryId, userId)) {
            return ResponseEntity.ok("Post liked");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User has already liked this post");
        }
    }

    @PostMapping("/{id}/comment")
    @ResponseBody
    public ResponseEntity<String> commentEntry(@PathVariable Long id, @RequestBody String comment) {
        // Проверка на пустой комментарий
        if (comment == null || comment.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Comment cannot be empty");
        }

        // Поиск поста по ID
        Optional<TheIdeaFactoryEntity> entityOptional = theIdeaFactoryRepository.findById(id);
        if (entityOptional.isPresent()) {
            TheIdeaFactoryEntity entity = entityOptional.get();
            entity.getComments().add(comment); // Добавление комментария
            theIdeaFactoryRepository.save(entity); // Сохранение изменений
            return ResponseEntity.ok("Comment added successfully");
        }

        // Обработка случая, если пост не найден
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
    }


    @GetMapping("/entries-with-comments")
    @ResponseBody
    public List<TheIdeaFactoryEntity> getAllEntriesWithComments() {
        return theIdeaFactoryService.getAllEntriesWithComments();
    }

}