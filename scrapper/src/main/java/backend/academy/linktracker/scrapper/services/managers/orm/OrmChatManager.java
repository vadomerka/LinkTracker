package backend.academy.linktracker.scrapper.services.managers.orm;

import backend.academy.linktracker.scrapper.models.entities.ChatEntity;
import backend.academy.linktracker.scrapper.models.entities.LinkEntity;
import backend.academy.linktracker.scrapper.models.exceptions.ChatNotFoundException;
import backend.academy.linktracker.scrapper.repositories.ChatLinkRepository;
import backend.academy.linktracker.scrapper.repositories.ChatRepository;
import backend.academy.linktracker.scrapper.services.managers.ChatManager;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class OrmChatManager implements ChatManager {

    private final ChatRepository chatRepository;
    private final ChatLinkRepository chatLinkRepository;

    public OrmChatManager(ChatRepository chatRepository, ChatLinkRepository chatLinkRepository) {
        this.chatRepository = chatRepository;
        this.chatLinkRepository = chatLinkRepository;
    }

    public void createChat(Long chatId) {
        ChatEntity chat = new ChatEntity(chatId);
        chatRepository.save(chat);
    }

    public Optional<ChatEntity> getChat(Long chatId) {
        return chatRepository.findById(chatId);
    }

    public List<ChatEntity> getAllChats() {
        return chatRepository.findAll();
    }

    public void deleteChat(Long chatId) {
        chatRepository.deleteById(chatId);
    }

    public boolean chatExists(Long chatId) {
        return chatRepository.existsById(chatId);
    }

    public List<LinkEntity> getContained(Long chatId, List<LinkEntity> links) {
        if (links.isEmpty()) {
            return List.of();
        }
        getChat(chatId).orElseThrow(ChatNotFoundException::new);
        var urls = links.stream().map(LinkEntity::getUrl).toList();
        return chatLinkRepository.findLinksByChatIdAndUrls(chatId, urls);
    }
}
