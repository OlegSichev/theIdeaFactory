package oleg.sichev.theideafactory.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.*;

@Entity
@Table(name = "entries")
public class TheIdeaFactoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private String username;

    @NotBlank(message = "Message cannot be blank")
    @Size(max = 10000, message = "Message cannot be longer than 10000 characters")
    @Column(nullable = false, length = 10000)
    private String message;

    @Column(length = 1000)
    private String answer;

    @Column(name = "approved", nullable = false)
    private boolean approved;

    @Column (name = "rejected", nullable = false)
    private boolean rejected;

    @Column (name = "anonymous", nullable = false)
    private boolean anonymous;

    @Column(name = "isDeleted", nullable = false)
    private boolean isDeleted;

    @Temporal(TemporalType.TIMESTAMP)
    private Date postedDate = new Date();

    @OneToMany(mappedBy = "entry", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<File> files = new ArrayList<>();

    @ManyToMany(mappedBy = "likedIdeas", cascade = CascadeType.ALL)
    private Set<User> usersWhoLiked = new HashSet<>();

    // Геттеры и сеттеры

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public boolean isRejected() {
        return rejected;
    }

    public void setRejected(boolean rejected) {
        this.rejected = rejected;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @OneToMany(mappedBy = "entry", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EntryTag> entryTags = new HashSet<>();

    // Метод для добавления тега в пост
    public void addTag(Tag tag) {
        EntryTag entryTag = new EntryTag();
        entryTag.setEntry(this);
        entryTag.setTag(tag);
        entryTags.add(entryTag);
    }

    // Метод для получения тегов
    public Set<Tag> getTags() {
        Set<Tag> tags = new HashSet<>();
        for (EntryTag entryTag : entryTags) {
            tags.add(entryTag.getTag());
        }
        return tags;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public Set<User> getUsersWhoLiked() {
        return usersWhoLiked;
    }

    public void setUsersWhoLiked(Set<User> usersWhoLiked) {
        this.usersWhoLiked = usersWhoLiked;
    }
}