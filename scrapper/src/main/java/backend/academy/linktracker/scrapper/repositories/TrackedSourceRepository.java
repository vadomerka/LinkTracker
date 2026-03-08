package backend.academy.linktracker.scrapper.repositories;

import backend.academy.linktracker.models.TrackedSource;
import backend.academy.linktracker.scrapper.models.exceptions.ChatAlreadyExistsException;
import backend.academy.linktracker.scrapper.models.exceptions.ChatNotFoundException;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TrackedSourceRepository {
    private final Map<Long, List<TrackedSource>> chats;

    public TrackedSourceRepository() {
        chats = new HashMap<>();
    }

    public List<TrackedSource> getChatLinks(Long chatId) {
        var arr = chats.get(chatId);
        if (arr == null) throw new ChatNotFoundException("Чат не был найден.");
        return arr;
    }

    public void addChat(Long chatId) {
        var arr = chats.get(chatId);
        if (arr != null) throw new ChatAlreadyExistsException("Чат уже существует.");
        chats.put(chatId, new ArrayList<>());
    }

    public void removeChat(Long chatId) {
        getChatLinks(chatId);
        chats.remove(chatId);
    }

    public void addLink(Long chatId, TrackedSource item) {
        var arr = getChatLinks(chatId);
        arr.add(item);
    }

    public void removeLink(Long chatId, TrackedSource item) {
        var arr = getChatLinks(chatId);
        arr.remove(item);
    }
}
