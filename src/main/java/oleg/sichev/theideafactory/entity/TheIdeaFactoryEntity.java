package oleg.sichev.theideafactory.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "entries")
public class TheIdeaFactoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, length = 1000)
    private String message;

    @Temporal(TemporalType.TIMESTAMP)
    private Date postedDate = new Date();

    @OneToMany(mappedBy = "entry", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<File> files;

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Date getPostedDate() { return postedDate; }
    public void setPostedDate(Date postedDate) { this.postedDate = postedDate; }
    public List<File> getFiles() { return files; }
    public void setFiles(List<File> files) { this.files = files; }
}