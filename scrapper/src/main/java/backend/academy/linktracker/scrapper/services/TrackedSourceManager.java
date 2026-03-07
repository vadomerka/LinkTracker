package backend.academy.linktracker.scrapper.services;

import backend.academy.linktracker.scrapper.factories.TrackedSourceFactory;
import backend.academy.linktracker.scrapper.models.requests.AddSourceRequest;
import backend.academy.linktracker.scrapper.models.requests.ListSourcesResponse;
import backend.academy.linktracker.scrapper.models.requests.RemoveSourceRequest;
import backend.academy.linktracker.scrapper.models.TrackedSource;
import backend.academy.linktracker.scrapper.models.exceptions.SourceNotFoundException;
import backend.academy.linktracker.scrapper.models.exceptions.SourceIsAlreadyTrackedException;
import backend.academy.linktracker.scrapper.repositories.TrackedSourceRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;

@Service
public class TrackedSourceManager {
    private final TrackedSourceRepository repository;
    private final TrackedSourceFactory factory;

    public TrackedSourceManager(TrackedSourceRepository repository, TrackedSourceFactory factory) {
        this.repository = repository;
        this.factory = factory;
    }

    public void addChat(Long chatId) {
        repository.addChat(chatId);
    }

    public void removeChat(Long chatId) {
        repository.removeChat(chatId);
    }

    public ListSourcesResponse getLinks(Long chatId) {
        var arr = repository.getChatLinks(chatId);
        return new ListSourcesResponse(arr, arr.size());
    }

    public boolean contains(List<TrackedSource> array, String url) {
        return !array.stream()
            .filter(ts -> ts.url().equalsIgnoreCase(url))
            .toList().isEmpty();
    }

    public TrackedSource addLink(Long chatId, AddSourceRequest req) {
        var links = repository.getChatLinks(chatId);
        if (contains(links, req.url()))
            throw new SourceIsAlreadyTrackedException("Ссылка уже отслеживается");

        var newSource = factory.create(req.url(), req.tags(), req.filters());
        links.add(newSource);
        return newSource;
    }

    public void removeLink(Long chatId, RemoveSourceRequest req) {
        var links = repository.getChatLinks(chatId);

        var wasRemoved = links.removeIf(ts -> Objects.equals(ts.url(), req.url()));
        if (!wasRemoved) throw new SourceNotFoundException("Cсылка не найдена");
    }
}
