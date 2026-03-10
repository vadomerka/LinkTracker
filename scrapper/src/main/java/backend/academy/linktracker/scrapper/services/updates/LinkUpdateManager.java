package backend.academy.linktracker.scrapper.services.updates;

import backend.academy.linktracker.scrapper.repositories.LinkUpdateRepository;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class LinkUpdateManager {
    private final LinkUpdateRepository repository;

    public LinkUpdateManager(LinkUpdateRepository repository) {
        this.repository = repository;
    }

    public Boolean isUpdated(String url, Instant time) {
        var lu = repository.getLastUpdated(url);
        repository.updateLink(url, time);
        return lu == null || lu.isAfter(time);
    }
}
