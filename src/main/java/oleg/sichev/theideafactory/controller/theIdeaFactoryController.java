package oleg.sichev.theideafactory.controller;

import oleg.sichev.theideafactory.entity.theIdeaFactoryEntity;
import oleg.sichev.theideafactory.service.theIdeaFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ResponseBody;


import java.util.List;

@Controller
public class theIdeaFactoryController {
    @Autowired
    private theIdeaFactoryService theIdeaFactoryService;

    @GetMapping("/theIdeaFactoryIndex")
    public String showGuestBook(Model model) {
        model.addAttribute("entries", theIdeaFactoryService.findAll());
        model.addAttribute("entry", new theIdeaFactoryEntity());
        return "theIdeaFactoryIndex";
    }

    @PostMapping("/theIdeaFactoryIndex")
    public String addEntry(@ModelAttribute theIdeaFactoryEntity theIdeaFactoryEntity) {
        theIdeaFactoryService.save(theIdeaFactoryEntity);
        return "redirect:/theIdeaFactoryIndex";
    }

    @GetMapping("/getEntriesFromDatabase")
    @ResponseBody
    public List<theIdeaFactoryEntity> getEntriesFromDatabase() {
        return theIdeaFactoryService.findAll(); // метод findAll() должен возвращать данные из базы данных
    }

}