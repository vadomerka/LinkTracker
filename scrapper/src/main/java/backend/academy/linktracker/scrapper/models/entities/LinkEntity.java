package backend.academy.linktracker.scrapper.models.entities;

import jakarta.persistence.Entity;
import java.time.Instant;
import java.util.List;

@Entity
public class LinkEntity {
    private String url;
    private List<ChatEntity> chats;
    private List<TagEntity> tags;
    private Instant lastUpdate;
}
