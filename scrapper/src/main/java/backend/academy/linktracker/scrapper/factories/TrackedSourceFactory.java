package backend.academy.linktracker.scrapper.factories;

import backend.academy.linktracker.models.TrackedSource;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TrackedSourceFactory {
    private Integer globalId = 0;

    public TrackedSourceFactory() {}

    public TrackedSource create(String url, List<String> tags, List<String> filters) {
        return new TrackedSource(++globalId, url, tags, filters);
    }
}
