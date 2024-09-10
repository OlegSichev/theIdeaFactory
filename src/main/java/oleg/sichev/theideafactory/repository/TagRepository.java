package oleg.sichev.theideafactory.repository;

import oleg.sichev.theideafactory.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findByName(String name); // Метод для поиска тега по имени
    List<Tag> findByNameContaining(String name); // Метод для поиска тегов по части имени
}