package oleg.sichev.theideafactory.service;

import oleg.sichev.theideafactory.entity.Tag;
import oleg.sichev.theideafactory.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    private static final Logger logger = LoggerFactory.getLogger(TagService.class);

    // Метод для создания или поиска существующего тега
    public Tag findOrCreateTag(String name) {
        try {
            // Убираем пробелы в имени
            Tag existingTag = tagRepository.findByName(name.trim());
            if (existingTag != null) {
                return existingTag;
            } else {
                Tag newTag = new Tag(name.trim());
                return tagRepository.save(newTag);
            }
        } catch (Exception e) {
            logger.error("Ошибка в методе findOrCreateTag", e);
            throw new RuntimeException("Ошибка поиска или создания тега", e);
        }
    }

    // Метод для получения всех тегов
    public List<Tag> findAll() {
        try {
            return tagRepository.findAll();
        } catch (Exception e) {
            logger.error("Ошибка получения всех тегов", e);
            throw new RuntimeException("Ошибка получения тегов", e);
        }
    }

    public void deleteById(Long tagId) {
        try {
            tagRepository.deleteById(tagId);
        } catch (Exception e) {
            logger.error("Ошибка удаления тега с id " + tagId, e);
            throw new RuntimeException("Ошибка удаления тега", e);
        }
    }

    // Новый метод для поиска тегов по части имени
    public List<Tag> findByNameContaining(String name) {
        try {
            return tagRepository.findByNameContaining(name.trim());
        } catch (Exception e) {
            logger.error("Ошибка поиска тегов по части имени", e);
            throw new RuntimeException("Ошибка поиска тегов", e);
        }
    }

    // Метод для поиска тега по ID
    public Tag findById(Long tagId) {
        try {
            Optional<Tag> tag = tagRepository.findById(tagId);
            return tag.orElse(null); // Возвращаем null, если тег не найден
        } catch (Exception e) {
            logger.error("Ошибка поиска тега с id " + tagId, e);
            throw new RuntimeException("Ошибка поиска тега", e);
        }
    }

    // Метод для сохранения тега
    public Tag save(Tag tag) {
        try {
            return tagRepository.save(tag);
        } catch (Exception e) {
            logger.error("Ошибка сохранения тега", e);
            throw new RuntimeException("Ошибка сохранения тега", e);
        }
    }
}
