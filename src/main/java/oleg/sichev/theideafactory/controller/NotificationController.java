package oleg.sichev.theideafactory.controller;

import oleg.sichev.theideafactory.entity.NotificationEntity;
import oleg.sichev.theideafactory.entity.TheIdeaFactoryEntity;
import oleg.sichev.theideafactory.repository.NotificationRepository;
import oleg.sichev.theideafactory.repository.TheIdeaFactoryRepository;
import oleg.sichev.theideafactory.security.CustomUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private TheIdeaFactoryRepository theIdeaFactoryRepository;

    @GetMapping
    public String listNotifications(Model model, Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<NotificationEntity> notifications = notificationRepository.findByUserIdAndIsReadFalseOrderByTimestampDesc(userId);
        List<NotificationEntity> filteredNotifications = new ArrayList<>();
        for (NotificationEntity notification : notifications) {
            TheIdeaFactoryEntity idea = theIdeaFactoryRepository.findById(notification.getPostId()).orElse(null);
            if (idea != null && !idea.isAnonymous()) {
                filteredNotifications.add(notification);
            }
        }
        model.addAttribute("notifications", filteredNotifications);
        return "notifications"; // notifications.html
    }

    @PostMapping("/clear")
    public String clearNotifications(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<NotificationEntity> notifications = notificationRepository.findByUserIdAndIsReadFalse(userId);
        for (NotificationEntity notification : notifications) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
        return "redirect:/notifications";
    }

    @GetMapping("/count") // Добавляем новый endpoint для получения количества уведомлений
    @ResponseBody // Ответ будет возвращен в виде JSON
    public ResponseEntity<Integer> getNotificationCount(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<NotificationEntity> notifications = notificationRepository.findByUserIdAndIsReadFalse(userId);
        int count = 0;
        for (NotificationEntity notification : notifications) {
            TheIdeaFactoryEntity idea = theIdeaFactoryRepository.findById(notification.getPostId()).orElse(null);
            if (idea != null && !idea.isAnonymous()) {
                count++;
            }
        }
        return ResponseEntity.ok(count);
    }
    private Long getUserIdFromAuthentication(Authentication authentication) {
        // Предположим, что ваш UserDetails имеет метод getId(), который возвращает ID пользователя
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }
}
