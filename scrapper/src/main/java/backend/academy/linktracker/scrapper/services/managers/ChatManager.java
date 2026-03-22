package backend.academy.linktracker.scrapper.services.managers;

import backend.academy.linktracker.scrapper.models.entities.ChatEntity;
import backend.academy.linktracker.scrapper.models.entities.LinkEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public interface ChatManager {
    void createChat(Long chatId);

    Optional<ChatEntity> getChat(Long chatId);

    List<ChatEntity> getAllChats();

    void deleteChat(Long chatId);

    boolean chatExists(Long chatId);

    List<LinkEntity> getContained(Long chatId, List<LinkEntity> links);
}
