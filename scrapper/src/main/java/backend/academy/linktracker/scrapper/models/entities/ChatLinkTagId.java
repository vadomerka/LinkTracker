package backend.academy.linktracker.scrapper.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import java.io.Serializable;

@Getter
@Embeddable
public class ChatLinkTagId implements Serializable {
    @Column(name = "chat_id")
    private final Long chatId;

    @Column(name = "link_url")
    private final String linkUrl;

    @Column(name = "tag_name")
    private final String tagName;

    public ChatLinkTagId(Long chatId, String linkUrl, String tagName) {
        this.chatId = chatId;
        this.linkUrl = linkUrl;
        this.tagName = tagName;
    }
}
