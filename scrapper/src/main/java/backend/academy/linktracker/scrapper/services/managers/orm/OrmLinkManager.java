package backend.academy.linktracker.scrapper.services.managers.orm;

import backend.academy.linktracker.scrapper.models.entities.LinkEntity;
import backend.academy.linktracker.scrapper.repositories.LinkRepository;
import backend.academy.linktracker.scrapper.services.managers.LinkManager;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class OrmLinkManager implements LinkManager {

    private final LinkRepository repository;

    public OrmLinkManager(LinkRepository repository) {
        this.repository = repository;
    }

    public LinkEntity createLink(String url) {
        LinkEntity link = new LinkEntity(url);
        return repository.save(link);
    }

    public Optional<LinkEntity> getLink(String url) {
        return repository.findById(url);
    }

    public List<LinkEntity> getAllLinks() {
        return repository.findAll();
    }

    public void deleteLink(String url) {
        repository.deleteById(url);
    }

    public boolean linkExists(String url) {
        return repository.existsById(url);
    }

    public boolean isUpdated(String url, Instant time) {
        var link = repository.getReferenceById(url);
        var lu = link.getLastUpdate();
        link.setLastUpdate(time);
        return lu == null || lu.isAfter(time);
    }
}
