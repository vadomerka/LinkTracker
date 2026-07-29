package backend.academy.linktracker.scrapper.repositories;

import backend.academy.linktracker.scrapper.models.entities.ChatLink;
import backend.academy.linktracker.scrapper.models.entities.ChatLinkId;
import backend.academy.linktracker.scrapper.models.entities.LinkEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatLinkRepository extends JpaRepository<ChatLink, ChatLinkId> {
    List<ChatLink> getByLink(LinkEntity entity);

    @Query("SELECT cl.link FROM ChatLink cl WHERE cl.id.chatId = :chatId AND cl.id.linkUrl IN :urls")
    List<LinkEntity> findLinksByChatIdAndUrls(@Param("chatId") Long chatId, @Param("urls") List<String> urls);
}
