package oleg.sichev.theideafactory.controller;

import oleg.sichev.theideafactory.entity.NotificationEntity;
import oleg.sichev.theideafactory.repository.NotificationRepository;
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

import java.util.List;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping
    public String listNotifications(Model model, Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<NotificationEntity> notifications = notificationRepository.findByUserIdAndIsReadFalseOrderByTimestampDesc(userId);
        model.addAttribute("notifications", notifications);
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
        int count = notificationRepository.countByUserIdAndIsReadFalse(userId);
        return ResponseEntity.ok(count);
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        // Предположим, что ваш UserDetails имеет метод getId(), который возвращает ID пользователя
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }
}