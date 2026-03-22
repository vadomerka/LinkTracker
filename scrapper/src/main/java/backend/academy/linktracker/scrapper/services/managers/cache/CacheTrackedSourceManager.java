package backend.academy.linktracker.scrapper.services.managers.cache;

import backend.academy.linktracker.models.LinkDto;
import backend.academy.linktracker.models.http.internal.AddSourceRequest;
import backend.academy.linktracker.models.http.internal.ListSourcesResponse;
import backend.academy.linktracker.models.http.internal.RemoveSourceRequest;
import backend.academy.linktracker.scrapper.factories.TrackedSourceFactory;
import backend.academy.linktracker.scrapper.models.TrackedSource;
import backend.academy.linktracker.scrapper.models.exceptions.SourceIsAlreadyTrackedException;
import backend.academy.linktracker.scrapper.models.exceptions.SourceNotFoundException;
import backend.academy.linktracker.scrapper.repositories.CacheChatLinkRepository;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class CacheTrackedSourceManager {
    private final CacheChatLinkRepository repository;
    private final TrackedSourceFactory factory;

    public CacheTrackedSourceManager(CacheChatLinkRepository repository, TrackedSourceFactory factory) {
        this.repository = repository;
        this.factory = factory;
    }

    public void addChat(Long chatId) {
        repository.addChat(chatId);
    }

    public void removeChat(Long chatId) {
        repository.removeChat(chatId);
    }

    public ListSourcesResponse getChatLinks(Long chatId, String tag) {
        var arr = repository.getChatLinks(chatId).stream();
        if (!Objects.equals(tag, "")) {
            arr = arr.filter(ts -> ts.tags().contains(tag));
        }
        var res = arr.map(ts -> new LinkDto(ts.url(), ts.tags())).toList();
        return new ListSourcesResponse(res, res.size());
    }

    public List<Long> getUniqueChats() {
        return repository.getChatIds();
    }

    public List<TrackedSource> getUniqueLinks() {
        return repository.getLinks();
    }

    public boolean contains(List<TrackedSource> array, String url) {
        return !array.stream()
                .filter(ts -> ts.url().equalsIgnoreCase(url))
                .toList()
                .isEmpty();
    }

    public List<String> filterChatLinks(Long chatId, List<String> filters) {
        var arr = repository.getChatLinks(chatId);
        return arr.stream().map(TrackedSource::url).filter(filters::contains).toList();
    }

    public TrackedSource addLink(Long chatId, AddSourceRequest req) {
        var links = repository.getChatLinks(chatId);
        if (contains(links, req.url())) throw new SourceIsAlreadyTrackedException("Ссылка уже отслеживается");

        var newSource = factory.create(req.url(), req.tags(), req.filters());
        repository.addLink(chatId, newSource);
        return newSource;
    }

    public void removeLink(Long chatId, RemoveSourceRequest req) {
        var links = repository.getChatLinks(chatId);

        var filtered =
                links.stream().filter(ts -> Objects.equals(ts.url(), req.url())).toList();
        if (filtered.isEmpty()) throw new SourceNotFoundException("Cсылка не найдена");
        repository.removeLink(chatId, filtered.getFirst());
    }
}
