package backend.academy.linktracker.scrapper.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
@Embeddable
public class ChatLinkTagId implements Serializable {
    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "link_url")
    private String linkUrl;

    @Column(name = "tag_name")
    private String tagName;

    public ChatLinkTagId() {}

    public ChatLinkTagId(Long chatId, String linkUrl, String tagName) {
        this.chatId = chatId;
        this.linkUrl = linkUrl;
        this.tagName = tagName;
    }
}
