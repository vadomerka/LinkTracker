package backend.academy.linktracker.scrapper.repositories;

import backend.academy.linktracker.scrapper.models.entities.ChatLinkTag;
import backend.academy.linktracker.scrapper.models.entities.ChatLinkTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatLinkTagRepository extends JpaRepository<ChatLinkTag, ChatLinkTagId> {
    List<ChatLinkTag> findAllByIdChatIdAndIdLinkUrl(Long chatId, String linkUrl);

    void deleteByIdChatIdAndIdLinkUrl(Long chatId, String linkUrl);

    boolean existsByIdChatIdAndIdLinkUrl(Long chatId, String linkUrl);
}
