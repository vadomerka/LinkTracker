package backend.academy.linktracker.scrapper.controllers.db;

import backend.academy.linktracker.models.LinkDto;
import backend.academy.linktracker.models.http.internal.AddSourceRequest;
import backend.academy.linktracker.models.http.internal.ListSourcesResponse;
import backend.academy.linktracker.models.http.internal.RemoveSourceRequest;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public interface DataController {

    void addChat(Long id);

    void removeChat(Long id);

    List<String> getLinks();

    void addLink(String url);

    void removeLink(String url);

    @NotNull
    ListSourcesResponse getChatLinks(Long tgChatId, String tag);

    @NotNull
    LinkDto addLink(Long tgChatId, AddSourceRequest req);

    void removeLink(Long tgChatId, RemoveSourceRequest req);
}
