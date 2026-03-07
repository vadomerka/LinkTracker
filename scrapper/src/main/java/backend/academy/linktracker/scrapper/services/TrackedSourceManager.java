package backend.academy.linktracker.scrapper.services;

import backend.academy.linktracker.scrapper.factories.TrackedSourceFactory;
import backend.academy.linktracker.scrapper.models.TrackedSource;
import backend.academy.linktracker.scrapper.models.TrackedSourceDTO;
import backend.academy.linktracker.scrapper.models.exceptions.UrlIsAlreadyTrackedException;
import backend.academy.linktracker.scrapper.repositories.TrackedSourceRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TrackedSourceManager {
    private final TrackedSourceRepository repository;
    private final TrackedSourceFactory factory;

    public TrackedSourceManager(TrackedSourceRepository repository, TrackedSourceFactory factory) {
        this.repository = repository;
        this.factory = factory;
    }

    public List<TrackedSource> getAll() {
        return repository.getArray();
    }

    public Optional<TrackedSource> findItem(String url) {
        return repository.getArray().stream()
            .filter(ts -> ts.url().equalsIgnoreCase(url))
            .findFirst();
    }

    public TrackedSource add(TrackedSourceDTO  dto) {
        // maybe add validation;
        if (findItem(dto.url()).isPresent())
            throw new UrlIsAlreadyTrackedException(String.format("Url %s уже отслеживается", dto.url()));

        var newSource = factory.create(dto.url(), dto.tags());
        repository.addItem(newSource);
        return newSource;
    }

    public void remove(String url) {
        throw new RuntimeException("Unavailable command!");
    }
}
