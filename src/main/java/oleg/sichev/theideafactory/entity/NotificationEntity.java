package oleg.sichev.theideafactory.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;  // Импорт для аннотации CreationTimestamp
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;         // ID пользователя, которому принадлежит уведомление

    @Column(nullable = false)
    private Long postId;         // ID поста, с которым связано уведомление

    @Column(nullable = false, length = 255)
    private String message;      // Сообщение уведомления

    @Column(nullable = false)
    private boolean isRead;      // Статус прочтения

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime timestamp;  // Время создания уведомления

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    // Другие методы
}