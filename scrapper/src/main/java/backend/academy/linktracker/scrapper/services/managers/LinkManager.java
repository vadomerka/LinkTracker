package backend.academy.linktracker.scrapper.services.managers;

import backend.academy.linktracker.scrapper.models.entities.LinkEntity;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public interface LinkManager {
    LinkEntity createLink(String url);

    Optional<LinkEntity> getLink(String url);

    List<LinkEntity> getAllLinks();

    void deleteLink(String url);

    boolean linkExists(String url);

    boolean isActive(String url);

    boolean isUpdated(String url, Instant time);
}
