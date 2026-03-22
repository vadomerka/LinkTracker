package backend.academy.linktracker.scrapper.services.updates;

import backend.academy.linktracker.scrapper.repositories.CacheChatLinkRepository;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class LinkUpdateManager {
    private final CacheChatLinkRepository repository;

    public LinkUpdateManager(CacheChatLinkRepository repository) {
        this.repository = repository;
    }

    public Boolean isUpdated(String url, Instant time) {
        var lu = repository.getLastUpdated(url);
        repository.updateLink(url, time);
        return lu == null || lu.isAfter(time);
    }
}
