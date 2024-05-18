package oleg.sichev.theideafactory.controller;

import oleg.sichev.theideafactory.entity.TheIdeaFactoryEntity;
import oleg.sichev.theideafactory.service.TheIdeaFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class TheIdeaFactoryAdminController {

    @Autowired
    private TheIdeaFactoryService theIdeaFactoryService;

    @GetMapping("/theIdeaFactoryIndexAdmin")
    public String showAdminGuestBook(Model model) {
        List<TheIdeaFactoryEntity> entries = theIdeaFactoryService.findAll();
        model.addAttribute("entries", entries);
        model.addAttribute("entry", new TheIdeaFactoryEntity());
        return "theIdeaFactoryIndexAdmin";
    }

    @PostMapping("/theIdeaFactoryIndexAdmin")
    public String addAdminEntry(TheIdeaFactoryEntity theIdeaFactoryEntity, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            theIdeaFactoryEntity.setUsername(authentication.getName()); // устанавливаем имя пользователя из аутентификации
        }
        theIdeaFactoryService.save(theIdeaFactoryEntity);
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
}
