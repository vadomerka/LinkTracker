package backend.academy.linktracker.scrapper.factories;

import backend.academy.linktracker.scrapper.models.TrackedSource;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TrackedSourceFactory {
    private static Integer globalId = 0;

    public TrackedSourceFactory() {}

    public TrackedSource create(String url, List<String> tags, List<String> filters) {
        return new TrackedSource(++globalId, url, tags, filters);
    }
}
