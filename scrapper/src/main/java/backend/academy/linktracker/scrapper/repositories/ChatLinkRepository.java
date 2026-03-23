package backend.academy.linktracker.scrapper.repositories;

import backend.academy.linktracker.scrapper.models.entities.ChatLink;
import backend.academy.linktracker.scrapper.models.entities.ChatLinkId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatLinkRepository extends JpaRepository<ChatLink, ChatLinkId> {}
