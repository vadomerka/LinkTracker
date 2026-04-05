package backend.academy.linktracker.scrapper.services.updates;

import backend.academy.linktracker.scrapper.models.updates.UpdateInfo;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class LinkUpdateInfoAnalyzeService {
    public Instant getUpdTime(UpdateInfo info) {
        // TODO: реализовать.
        return Instant.parse("2026-04-05T18:28:31Z");
    }
}
