package backend.academy.linktracker.scrapper.repositories;

import backend.academy.linktracker.models.TrackedSource;
import java.time.Instant;
import java.util.List;

public interface ChatLinkRepository {
    public List<Long> getChatIds();
    public List<TrackedSource> getLinks();
    public List<TrackedSource> getChatLinks(Long chatId);
    public void addChat(Long chatId);
    public void removeChat(Long chatId);
    public void addLink(Long chatId, TrackedSource link);
    public void removeLink(Long chatId, TrackedSource link);

    public Instant getLastUpdated(String url);
    public void updateLink(String url, Instant time);
}
