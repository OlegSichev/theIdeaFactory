package oleg.sichev.theideafactory.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany
    @JoinTable(
            name = "entry_tags",
            joinColumns = @JoinColumn(name = "tag_id"),
            inverseJoinColumns = @JoinColumn(name = "entry_id")
    )
    @JsonBackReference
    private Set<TheIdeaFactoryEntity> entries = new HashSet<>();

    // Конструктор по умолчанию
    public Tag() {}

    // Альтернативный конструктор для инициализации имени
    public Tag(String name) {
        this.name = name;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<TheIdeaFactoryEntity> getEntries() {
        return entries;
    }

    public void setEntries(Set<TheIdeaFactoryEntity> entries) {
        this.entries = entries;
    }
}