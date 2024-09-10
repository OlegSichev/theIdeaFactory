package oleg.sichev.theideafactory.controller;

import oleg.sichev.theideafactory.entity.User;
import oleg.sichev.theideafactory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserProfileController {

    @Autowired
    private UserService userService;

    // Отображение профиля текущего пользователя
    @GetMapping("/profile")
    public String viewProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User user = userService.findByUsername(currentUsername);

        if (user == null) {
            return "redirect:/login"; // или другая страница с ошибкой
        }

        model.addAttribute("user", user);
        return "profile"; // убедитесь, что у вас есть файл templates/profile.html
    }

    // Изменение пароля
    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam("password") String newPassword,
            RedirectAttributes redirectAttributes) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User user = userService.findByUsername(currentUsername);

        if (user == null) {
            return "redirect:/login"; // или другая страница с ошибкой
        }

        // Измените пароль пользователя
        userService.updatePassword(user, newPassword);

        redirectAttributes.addFlashAttribute("success", "Пароль успешно изменен.");

        return "redirect:/profile";
    }
}