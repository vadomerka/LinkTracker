package backend.academy.linktracker.scrapper.repositories;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class LinkUpdateRepository {  // implements Repository<T, ID>
    private Map<String, Instant> links;

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
