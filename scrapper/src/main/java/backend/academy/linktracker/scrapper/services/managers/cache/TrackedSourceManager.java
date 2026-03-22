package backend.academy.linktracker.scrapper.services.managers.cache;

import backend.academy.linktracker.models.LinkDto;
import backend.academy.linktracker.models.http.internal.ListSourcesResponse;
import backend.academy.linktracker.scrapper.factories.TrackedSourceFactory;
import backend.academy.linktracker.scrapper.models.TrackedSource;
import backend.academy.linktracker.scrapper.models.entities.ChatEntity;
import backend.academy.linktracker.scrapper.models.exceptions.ChatNotFoundException;
import backend.academy.linktracker.scrapper.services.managers.orm.ORMChatManager;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TrackedSourceManager {
    private final ORMChatManager ORMChatManager;
    private final TrackedSourceFactory factory;

    public TrackedSourceManager(ORMChatManager ORMChatManager, TrackedSourceFactory factory) {
        this.ORMChatManager = ORMChatManager;
        this.factory = factory;
    }

    public void addChat(Long chatId) {
        ORMChatManager.createChat(chatId);
    }

    public void removeChat(Long chatId) {
        ORMChatManager.deleteChat(chatId);
    }

    public ListSourcesResponse getChatLinks(Long chatId, String tag) {
        var chat = ORMChatManager.getChat(chatId);
        if (chat.isEmpty()) throw new ChatNotFoundException("Чат не был найден.");

        var arr = chat.get().getLinks().stream()
                .map(le -> new LinkDto(le.getUrl(), le.getTagNames()))
                .toList();
        return new ListSourcesResponse(arr, arr.size());
    }

    public List<Long> getUniqueChats() {
        return ORMChatManager.getAllChats().stream().map(ChatEntity::getChatId).toList();
    }

    public List<TrackedSource> getUniqueLinks() {
        return null;
    }

    public boolean contains(List<TrackedSource> array, String url) {
        return !array.stream()
                .filter(ts -> ts.url().equalsIgnoreCase(url))
                .toList()
                .isEmpty();
    }

    //    public List<String> filterChatLinks(Long chatId, List<String> filters) {
    //        var arr = repository.getChatLinks(chatId);
    //        return arr.stream().map(TrackedSource::url).filter(filters::contains).toList();
    //    }
    //
    //    public TrackedSource addLink(Long chatId, AddSourceRequest req) {
    //        var links = repository.getChatLinks(chatId);
    //        if (contains(links, req.url())) throw new SourceIsAlreadyTrackedException("Ссылка уже отслеживается");
    //
    //        var newSource = factory.create(req.url(), req.tags(), req.filters());
    //        repository.addLink(chatId, newSource);
    //        return newSource;
    //    }
    //
    //    public void removeLink(Long chatId, RemoveSourceRequest req) {
    //        var links = repository.getChatLinks(chatId);
    //
    //        var filtered =
    //                links.stream().filter(ts -> Objects.equals(ts.url(), req.url())).toList();
    //        if (filtered.isEmpty()) throw new SourceNotFoundException("Cсылка не найдена");
    //        repository.removeLink(chatId, filtered.getFirst());
    //    }
}
