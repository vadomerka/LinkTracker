package backend.academy.linktracker.scrapper.repositories;

import backend.academy.linktracker.scrapper.models.TrackedSource;
import backend.academy.linktracker.scrapper.models.exceptions.ChatAlreadyExistsException;
import backend.academy.linktracker.scrapper.models.exceptions.ChatNotFoundException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class CacheChatLinkRepository implements ChatLinkRepository {
    private final Map<Long, List<TrackedSource>> chatLinks;
    private final Map<TrackedSource, List<Long>> linkChats;
    private final Map<String, Instant> linksUpdated;

    public CacheChatLinkRepository() {
        chatLinks = new HashMap<>();
        linkChats = new HashMap<>();
        linksUpdated = new HashMap<>();
    }

    public List<Long> getChatIds() {
        return chatLinks.keySet().stream().toList();
    }

    public List<TrackedSource> getLinks() {
        return linkChats.keySet().stream().toList();
    }

    public List<TrackedSource> getChatLinks(Long chatId) {
        var arr = chatLinks.get(chatId);
        if (arr == null) throw new ChatNotFoundException("Чат не был найден.");
        return arr;
    }

    public void addChat(Long chatId) {
        var arr = chatLinks.get(chatId);
        if (arr != null) throw new ChatAlreadyExistsException("Чат уже существует.");
        chatLinks.put(chatId, new ArrayList<>());
    }

    public void removeChat(Long chatId) {
        var links = getChatLinks(chatId);
        for (var l : links) {
            linkChats.get(l).remove(chatId);
        }
        chatLinks.remove(chatId);
    }

    public void addLink(Long chatId, TrackedSource link) {
        chatLinks.get(chatId).add(link);

        var lChats = linkChats.get(link);
        if (lChats == null) lChats = new ArrayList<>();
        lChats.add(chatId);
        linkChats.put(link, lChats);
    }

    public void removeLink(Long chatId, TrackedSource link) {
        getChatLinks(chatId).remove(link);

        var lChats = linkChats.get(link);
        lChats.remove(chatId);
        if (lChats.isEmpty()) linkChats.remove(link);
    }

    public Instant getLastUpdated(String url) {
        return linksUpdated.get(url);
    }

    public void updateLink(String url, Instant time) {
        linksUpdated.put(url, time);
    }
}
