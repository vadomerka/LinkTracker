package backend.academy.linktracker.scrapper.services.managers.orm;

import backend.academy.linktracker.scrapper.models.entities.ChatEntity;
import backend.academy.linktracker.scrapper.models.entities.LinkEntity;
import backend.academy.linktracker.scrapper.models.exceptions.ChatNotFoundException;
import backend.academy.linktracker.scrapper.repositories.ChatRepository;
import backend.academy.linktracker.scrapper.services.managers.ChatManager;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrmChatManager implements ChatManager {

    private final ChatRepository chatRepository;

    public OrmChatManager(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
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
        var chat = getChat(chatId).orElseThrow(ChatNotFoundException::new);
        return chat.getLinks().stream().filter(links::contains).toList();
    }

    public List<LinkEntity> getLinks(Long chatId) {
        var chat = getChat(chatId).orElseThrow(ChatNotFoundException::new);
        return chat.getLinks();
    }
}
