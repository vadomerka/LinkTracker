package backend.academy.linktracker.scrapper.services;

import backend.academy.linktracker.scrapper.repositories.LinkUpdateRepository;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
public class LinkUpdateManager {
    private LinkUpdateRepository repository;

    public LinkUpdateManager(LinkUpdateRepository repository) {
        this.repository = repository;
    }

    public Boolean isUpdated(String url, Instant time) {
        var lu = repository.getLastUpdated(url);
        repository.updateLink(url, time);
        return lu == null || lu != time;
    }
}
