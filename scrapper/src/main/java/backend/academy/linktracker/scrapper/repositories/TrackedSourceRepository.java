package backend.academy.linktracker.scrapper.repositories;

import backend.academy.linktracker.models.TrackedSource;
import backend.academy.linktracker.scrapper.models.exceptions.ChatAlreadyExistsException;
import backend.academy.linktracker.scrapper.models.exceptions.ChatNotFoundException;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Repository
public class TrackedSourceRepository {
    private final Map<Long, List<TrackedSource>> chatLinks;
    private final Map<TrackedSource, List<Long>> linkChats;

    public TrackedSourceRepository() {
        chatLinks = new HashMap<>();
        linkChats = new HashMap<>();
    }

    public List<TrackedSource> getLinks() {
        return linkChats.keySet().stream().toList();
    }

    public List<TrackedSource> getChatLinks(Long chatId) {
        var arr = chatLinks.get(chatId);
        if (arr == null) throw new ChatNotFoundException("Чат не был найден.");
        return arr;
    }

    public List<Long> getLinkChats(String link) {
        var ans = new HashSet<Long>();
        for (var k: chatLinks.keySet()) {
            if (chatLinks.get(k).stream()
                .map(ts -> ts.url().toLowerCase()).toList()
                .contains(link)) {
                ans.add(k);
            }
        }
        return ans.stream().toList();
    }

    public void addChat(Long chatId) {
        var arr = chatLinks.get(chatId);
        if (arr != null) throw new ChatAlreadyExistsException("Чат уже существует.");
        chatLinks.put(chatId, new ArrayList<>());
    }

    public void removeChat(Long chatId) {
        var links = getChatLinks(chatId);
        for (var l: links) {
            linkChats.get(l).remove(chatId);
        }
        chatLinks.remove(chatId);
    }

    public void addLink(Long chatId, TrackedSource link) {
        var arr = getChatLinks(chatId);
        arr.add(link);

        var lChats = linkChats.get(link);
        if (lChats == null) lChats = new ArrayList<>();
        lChats.add(chatId);
        linkChats.put(link, lChats);
    }

    public void removeLink(Long chatId, TrackedSource link) {
        var arr = getChatLinks(chatId);
        arr.remove(link);

        var lChats = linkChats.get(link);
        lChats.remove(chatId);
        if (lChats.isEmpty()) linkChats.remove(link);
    }
}
