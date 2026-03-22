package backend.academy.linktracker.scrapper.models.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tag")
public class TagEntity {
    @Id
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatLinkTag> chatLinkTags = new ArrayList<>();
}
