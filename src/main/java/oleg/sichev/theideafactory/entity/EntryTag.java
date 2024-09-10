package oleg.sichev.theideafactory.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "entry_tags")
public class EntryTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "entry_id", nullable = false)
    private TheIdeaFactoryEntity entry;

    @ManyToOne
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    // Дополнительные поля, если необходимо (например, дата добавления)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate = new Date();

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TheIdeaFactoryEntity getEntry() {
        return entry;
    }

    public void setEntry(TheIdeaFactoryEntity entry) {
        this.entry = entry;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}