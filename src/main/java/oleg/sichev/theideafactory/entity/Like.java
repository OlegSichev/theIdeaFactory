package oleg.sichev.theideafactory.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "likes")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "the_idea_factory_entity_id")
    private TheIdeaFactoryEntity theIdeaFactoryEntity;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Геттеры и сеттеры


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TheIdeaFactoryEntity getTheIdeaFactoryEntity() {
        return theIdeaFactoryEntity;
    }

    public void setTheIdeaFactoryEntity(TheIdeaFactoryEntity theIdeaFactoryEntity) {
        this.theIdeaFactoryEntity = theIdeaFactoryEntity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

