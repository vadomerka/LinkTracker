package backend.academy.linktracker.scrapper.models.entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "chat_link")
public class ChatLink {
    @EmbeddedId
    private ChatLinkId id;

    @ManyToOne
    @MapsId("chatId")
    @JoinColumn(name = "chat_id", insertable = false, updatable = false)
    private ChatEntity chat;

    @ManyToOne
    @MapsId("linkUrl")
    @JoinColumn(name = "link_url", insertable = false, updatable = false)
    private LinkEntity link;

    @OneToMany(mappedBy = "chat_link", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatLinkTag> chatLinkTags = new ArrayList<>();

    public List<TagEntity> getTags() {
        return chatLinkTags.stream().map(ChatLinkTag::getTag).distinct().toList();
    }

    public ChatLink(ChatEntity chat, LinkEntity link) {
        this.id = new ChatLinkId(chat.getChatId(), link.getUrl());
        this.chat = chat;
        this.link = link;
    }
}
