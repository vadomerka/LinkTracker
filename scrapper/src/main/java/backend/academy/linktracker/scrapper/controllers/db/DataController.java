package backend.academy.linktracker.scrapper.controllers.db;

import backend.academy.linktracker.models.LinkDto;
import backend.academy.linktracker.models.http.internal.AddSourceRequest;
import backend.academy.linktracker.models.http.internal.ListSourcesResponse;
import backend.academy.linktracker.models.http.internal.RemoveSourceRequest;
import org.jetbrains.annotations.NotNull;

public interface DataController {

    void addChat(Long id);

    void removeChat(Long id);

    @NotNull ListSourcesResponse getChatLinks(Long tgChatId, String tag);

    @NotNull LinkDto addLink(Long tgChatId, AddSourceRequest req);

    void removeLink(Long tgChatId, RemoveSourceRequest req);
}
