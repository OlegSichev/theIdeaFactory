package oleg.sichev.theideafactory.controller;

import oleg.sichev.theideafactory.entity.TheIdeaFactoryEntity;
import oleg.sichev.theideafactory.service.TheIdeaFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class TheIdeaFactoryController {

    @Autowired
    private TheIdeaFactoryService theIdeaFactoryService;

    @GetMapping("/theIdeaFactoryIndex")
    public String showGuestBook(Model model) {
        List<TheIdeaFactoryEntity> entries = theIdeaFactoryService.findAll();
        model.addAttribute("entries", entries);
        model.addAttribute("entry", new TheIdeaFactoryEntity());
        return "theIdeaFactoryIndex";
    }

    @PostMapping("/theIdeaFactoryIndex")
    public String addEntry(TheIdeaFactoryEntity theIdeaFactoryEntity, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            theIdeaFactoryEntity.setUsername(authentication.getName()); // устанавливаем имя пользователя из аутентификации
        }
        theIdeaFactoryService.save(theIdeaFactoryEntity);
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
}