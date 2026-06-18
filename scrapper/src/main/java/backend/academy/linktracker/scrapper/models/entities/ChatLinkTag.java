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
    @JoinColumns({
        @JoinColumn(name = "chat_id", referencedColumnName = "chat_id", insertable = false, updatable = false),
        @JoinColumn(name = "link_url", referencedColumnName = "link_url", insertable = false, updatable = false)
    })
    private ChatLink chatLink;

    @ManyToOne
    @MapsId("tagName")
    @JoinColumn(name = "tag_name", insertable = false, updatable = false)
    private TagEntity tag;

    public ChatLinkTag() {}

    public ChatLinkTag(ChatLink chatLink, TagEntity tag) {
        this.id =
                new ChatLinkTagId(chatLink.getId().getChatId(), chatLink.getId().getLinkUrl(), tag.getName());
        this.chatLink = chatLink;
        this.tag = tag;
    }
}
