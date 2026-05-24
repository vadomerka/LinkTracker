package backend.academy.linktracker.scrapper.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
@Embeddable
public class ChatLinkId implements Serializable {
    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "link_url")
    private String linkUrl;

    public ChatLinkId() {}

    public ChatLinkId(Long chatId, String linkUrl) {
        this.chatId = chatId;
        this.linkUrl = linkUrl;
    }
}
