package backend.academy.linktracker.scrapper.models.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "chat")
public class ChatEntity {
    @Id
    @Column(name = "chat_id", nullable = false, unique = true)
    private Long chatId;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatLink> chatLinks = new ArrayList<>();

    public ChatEntity() {}

    public ChatEntity(Long chatId) {
        this.chatId = chatId;
    }

    public List<LinkEntity> getLinks() {
        return chatLinks.stream().map(ChatLink::getLink).distinct().toList();
    }
}
