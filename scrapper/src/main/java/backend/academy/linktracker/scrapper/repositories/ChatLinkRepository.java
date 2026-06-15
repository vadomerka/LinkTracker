package backend.academy.linktracker.scrapper.repositories;

import backend.academy.linktracker.scrapper.models.entities.ChatLink;
import backend.academy.linktracker.scrapper.models.entities.ChatLinkId;
import backend.academy.linktracker.scrapper.models.entities.LinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatLinkRepository extends JpaRepository<ChatLink, ChatLinkId> {
    List<ChatLink> getByLink(LinkEntity entity);
}
