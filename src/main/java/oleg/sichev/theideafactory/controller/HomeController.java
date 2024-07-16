package oleg.sichev.theideafactory.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;

@Controller
public class HomeController {
    @GetMapping("/")
    public String greeting(Model model, Authentication authentication) {
        // Проверка роли пользователя
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            boolean isAdmin = authorities.stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
            model.addAttribute("isAdmin", isAdmin);
        }

        // Поскольку метод возвращает "news", я предполагаю, что другие необходимые атрибуты для страницы "news"
        // должны быть добавлены в аналогичном подходе, если они нужны.

        return "news"; // Здесь предполагается, что домашняя страница также является страницей новостей
    }

    @GetMapping("/theIdeaFactoryIndexAdmin")
    public String postsTheIdeaFactoryIndexForAdmin() {
        return "theIdeaFactoryIndexAdmin";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/manager")
    public String manager() {
        return "manager";
    }

    @GetMapping("/employee")
    public String employee() {
        return "employee";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login";
    }
}