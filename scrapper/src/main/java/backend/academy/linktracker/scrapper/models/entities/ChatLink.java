package backend.academy.linktracker.scrapper.models.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

    @OneToMany(mappedBy = "chatLink", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatLinkTag> chatLinkTags = new ArrayList<>();

    public ChatLink() {}

    public ChatLink(ChatEntity chat, LinkEntity link) {
        this.id = new ChatLinkId(chat.getChatId(), link.getUrl());
        this.chat = chat;
        this.link = link;
    }

    public List<TagEntity> getTags() {
        return chatLinkTags.stream().map(ChatLinkTag::getTag).distinct().toList();
    }
}
