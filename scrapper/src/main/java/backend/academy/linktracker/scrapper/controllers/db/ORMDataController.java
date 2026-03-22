package backend.academy.linktracker.scrapper.controllers.db;

import backend.academy.linktracker.models.LinkDto;
import backend.academy.linktracker.models.http.internal.AddSourceRequest;
import backend.academy.linktracker.models.http.internal.ListSourcesResponse;
import backend.academy.linktracker.models.http.internal.RemoveSourceRequest;
import backend.academy.linktracker.scrapper.models.exceptions.ChatNotFoundException;
import backend.academy.linktracker.scrapper.models.exceptions.SourceIsAlreadyTrackedException;
import backend.academy.linktracker.scrapper.services.managers.ChatLinkManager;
import backend.academy.linktracker.scrapper.services.managers.ChatManager;
import backend.academy.linktracker.scrapper.services.managers.LinkManager;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class ORMDataController implements DataController {
    private final ChatManager chatManager;
    private final LinkManager linkManager;
    private final ChatLinkManager clManager;

    public ORMDataController(ChatManager chatManager, LinkManager linkManager, ChatLinkManager clManager) {
        this.chatManager = chatManager;
        this.linkManager = linkManager;
        this.clManager = clManager;
    }

    public void addChat(Long chatId) { chatManager.createChat(chatId); }

    public void removeChat(Long chatId) { chatManager.deleteChat(chatId); }

    public @NotNull ListSourcesResponse getChatLinks(Long chatId, String tag) {
        var arr = chatManager.getTaggedLinks(chatId, tag);
        var res = arr.stream().map(le -> new LinkDto(le.getUrl(), le.getTagNames())).toList();
        return new ListSourcesResponse(res, res.size());
    }

    public @NotNull LinkDto addLink(Long chatId, AddSourceRequest req) {
        var chat = chatManager.getChat(chatId);
        if (chat.isEmpty()) throw new ChatNotFoundException("Чат не найден.");
        var res = clManager.addLink(chatId, req.url());
        if (res == null) throw new SourceIsAlreadyTrackedException("Ссылка уже добавлена.");
        return new LinkDto(res.getUrl(), res.getTagNames());
    }

    public void removeLink(Long chatId, RemoveSourceRequest req) {
        var chat = chatManager.getChat(chatId);
        if (chat.isEmpty()) throw new ChatNotFoundException("Чат не найден.");
        clManager.removeLink(chatId, req.url());
    }
}
