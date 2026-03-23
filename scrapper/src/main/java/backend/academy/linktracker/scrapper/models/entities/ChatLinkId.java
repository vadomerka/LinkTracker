package backend.academy.linktracker.scrapper.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.Getter;

@Getter
@Embeddable
public class ChatLinkId implements Serializable {
    @Column(name = "chat_id")
    private final Long chatId;

    @Column(name = "link_url")
    private final String linkUrl;

    public ChatLinkId(Long chatId, String linkUrl) {
        this.chatId = chatId;
        this.linkUrl = linkUrl;
    }
}
