package backend.academy.linktracker.scrapper.models.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "link")
public class LinkEntity {
    @Id
    @Column(name = "url", nullable = false, unique = true)
    private String url;

    @Column(name = "last_update")
    private Instant lastUpdate;

    @OneToMany(mappedBy = "link", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatLinkTag> chatLinkTags = new ArrayList<>();
}
