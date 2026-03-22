package backend.academy.linktracker.scrapper.models.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

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

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "link", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatLinkTag> chatLinkTags = new ArrayList<>();

    public List<ChatEntity> getChats() {
        return chatLinkTags.stream().map(ChatLinkTag::getChat).distinct().toList();
    }

    public List<String> getTagNames() {
        return chatLinkTags.stream()
                .map(ChatLinkTag::getTag)
                .map(TagEntity::getName)
                .distinct()
                .toList();
    }

    public LinkEntity(String url) {
        this.url = url;
    }
}
