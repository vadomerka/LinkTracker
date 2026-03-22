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
    @JoinColumn(name = "chat_id")
    private ChatEntity chat;

    @ManyToOne
    @MapsId("linkUrl")
    @JoinColumn(name = "link_url")
    private LinkEntity link;

    @ManyToOne
    @MapsId("tagName")
    @JoinColumn(name = "tag_name")
    private TagEntity tag;
}
