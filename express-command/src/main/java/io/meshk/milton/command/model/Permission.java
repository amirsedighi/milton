package io.meshk.milton.command.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
public class Permission {

    @Id
    //GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "permissionSeqGen", sequenceName = "permissionSequence", initialValue = 1, allocationSize = 1)
    @GeneratedValue(generator = "permissionSeqGen")
    private Long id;

    private String title;

    private String description;

    private LocalDate publishDate;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    public Permission() {
    }

}