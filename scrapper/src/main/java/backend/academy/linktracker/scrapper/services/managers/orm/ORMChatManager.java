package backend.academy.linktracker.scrapper.services.managers.orm;

import backend.academy.linktracker.scrapper.models.entities.ChatEntity;
import backend.academy.linktracker.scrapper.models.entities.LinkEntity;
import backend.academy.linktracker.scrapper.models.exceptions.ChatNotFoundException;
import backend.academy.linktracker.scrapper.repositories.ChatRepository;
import backend.academy.linktracker.scrapper.services.managers.ChatManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ORMChatManager implements ChatManager {

    private final ChatRepository chatRepository;

    public ORMChatManager(ChatRepository chatRepository) {
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
        var chat = getChat(chatId);
        if (chat.isEmpty()) throw new ChatNotFoundException("Чат не найден.");
        return chat.get().getLinks().stream().filter(links::contains).toList();
    }

    public List<LinkEntity> getTaggedLinks(Long chatId, String tag) {
        var chat = getChat(chatId);
        if (chat.isEmpty()) throw new ChatNotFoundException("Чат не найден.");
        var links = chat.get().getLinks();
        var ans = new ArrayList<LinkEntity>();
        for (var l: links) {
            if (l.getTagNames().contains(tag)) { ans.add(l); }
        }
        return ans;
    }
}
