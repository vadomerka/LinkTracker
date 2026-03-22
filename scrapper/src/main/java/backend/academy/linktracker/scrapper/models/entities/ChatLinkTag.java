package backend.academy.linktracker.scrapper.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "chat_link_tag")
public class ChatLinkTag {
    @EmbeddedId
    private ChatLinkTagId id;

    @ManyToOne
    @MapsId("chatId")
    @JoinColumn(name = "chat_id", insertable = false, updatable = false)
    private ChatEntity chat;

    @ManyToOne
    @MapsId("linkUrl")
    @JoinColumn(name = "link_url", insertable = false, updatable = false)
    private LinkEntity link;

    @ManyToOne
    @MapsId("tagName")
    @JoinColumn(name = "tag_name", insertable = false, updatable = false)
    private TagEntity tag;

    public ChatLinkTag() {}

    public ChatLinkTag(ChatEntity chat, LinkEntity link, TagEntity tag) {
        this.id = new ChatLinkTagId(chat.getChatId(), link.getUrl(), tag.getName());
        this.chat = chat;
        this.link = link;
        this.tag = tag;
    }
}
