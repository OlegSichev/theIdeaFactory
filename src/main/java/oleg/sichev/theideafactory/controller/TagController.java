package oleg.sichev.theideafactory.controller;

import oleg.sichev.theideafactory.entity.Tag;
import oleg.sichev.theideafactory.service.TagService;
import oleg.sichev.theideafactory.service.TheIdeaFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController // Заменяем Controller на RestController, что бы в методе getTags отдавался json, а не html
public class TagController {

    @Autowired
    private TagService tagService;

    @Autowired
    private TheIdeaFactoryService theIdeaFactoryService;

    @GetMapping("api/tags")
    public List<Tag> getTags() {
        return tagService.findAll(); // Возврат списка тегов в формате JSON
    }

    @PostMapping("/tags")
    public String addTag(@RequestParam String tagName) {
        tagService.findOrCreateTag(tagName); // Создаем или находим тег
        return "Тег добавлен"; // Можно вернуть больше информации
    }

    @DeleteMapping("/api/tags/{tagId}")
    public String deleteTag(@PathVariable Long tagId) {
        tagService.deleteById(tagId);
        return "Тег удален";
    }

    // Новый метод для создания поста с тегами
    @PostMapping("/createPostWithTags")
    public String createPostWithTags(
            @RequestParam Integer userId,
            @RequestParam String username,
            @RequestParam String message,
            @RequestParam(required = false, defaultValue = "") String tags,
            @RequestParam(name = "anonymous", required = false, defaultValue = "false") boolean anonymous // Добавляем параметр anonymous
    ) throws IOException {

        List<String> tagNames = Arrays.asList(tags.split(","));
        theIdeaFactoryService.createPostWithTags(userId, username, message, tagNames, anonymous); // Создаем пост с тегами

        return "Пост создан"; // Можно вернуть больше информации
    }

    // Метод для поиска тегов по названию
    @GetMapping("/api/tags/search")
    public List<Tag> searchTagsByName(@RequestParam String tagName) {
        return tagService.findByNameContaining(tagName); // Используем метод из TagService
    }

    // Метод для редактирования тега
    @PutMapping("/api/tags/{tagId}")
    public Tag updateTag(@PathVariable Long tagId, @RequestParam String newName) {
        Tag tag = tagService.findById(tagId);
        if (tag != null) {
            tag.setName(newName);
            return tagService.save(tag);
        } else {
            return null; // Возвращаем null, если тег не найден
        }
    }
}