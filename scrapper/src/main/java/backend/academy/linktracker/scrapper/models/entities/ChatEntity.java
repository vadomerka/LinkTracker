package backend.academy.linktracker.scrapper.models.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "chat")
public class ChatEntity {
    @Id
    @Column(name = "chat_id", nullable = false, unique = true)
    private Long chatId;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatLinkTag> chatLinkTags = new ArrayList<>();

    public List<LinkEntity> getLinks() {
        return chatLinkTags.stream()
            .map(ChatLinkTag::getLink)
            .distinct()
            .toList();
    }
}
