package backend.academy.linktracker.scrapper.repositories;

import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Repository
public class LinkUpdateRepository {
    private final Map<String, Instant> links;

    public LinkUpdateRepository() {
        links = new HashMap<>();
    }

    public Instant getLastUpdated(String url) {
        return links.get(url);
    }

    public void updateLink(String url, Instant time) {
        links.put(url, time);
    }
}
