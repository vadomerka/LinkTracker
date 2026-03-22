package backend.academy.linktracker.scrapper.models.entities;

import jakarta.persistence.Entity;
import java.util.List;

@Entity
public class ChatEntity {
    private Long chatId;
    private List<LinkEntity> links;
}
