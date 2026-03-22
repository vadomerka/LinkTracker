package backend.academy.linktracker.scrapper.repositories;

import backend.academy.linktracker.scrapper.models.TrackedSource;
import java.time.Instant;
import java.util.List;

public interface ChatLinkRepository {
    List<Long> getChatIds();

    List<TrackedSource> getLinks();

    List<TrackedSource> getChatLinks(Long chatId);

    void addChat(Long chatId);

    void removeChat(Long chatId);

    void addLink(Long chatId, TrackedSource link);

    void removeLink(Long chatId, TrackedSource link);

    Instant getLastUpdated(String url);

    void updateLink(String url, Instant time);
}
