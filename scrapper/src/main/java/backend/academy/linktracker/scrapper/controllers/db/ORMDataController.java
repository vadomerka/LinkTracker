package backend.academy.linktracker.scrapper.controllers.db;

import backend.academy.linktracker.models.LinkDto;
import backend.academy.linktracker.models.http.internal.AddSourceRequest;
import backend.academy.linktracker.models.http.internal.ListSourcesResponse;
import backend.academy.linktracker.models.http.internal.RemoveSourceRequest;
import backend.academy.linktracker.scrapper.models.entities.TagEntity;
import backend.academy.linktracker.scrapper.models.exceptions.ChatNotFoundException;
import backend.academy.linktracker.scrapper.services.managers.ChatLinkManager;
import backend.academy.linktracker.scrapper.services.managers.ChatLinkTagManager;
import backend.academy.linktracker.scrapper.services.managers.ChatManager;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class ORMDataController implements DataController {
    private final ChatManager chatManager;
    private final ChatLinkManager clManager;
    private final ChatLinkTagManager cltManager;

    public ORMDataController(ChatManager chatManager, ChatLinkManager clManager, ChatLinkTagManager cltManager) {
        this.chatManager = chatManager;
        this.clManager = clManager;
        this.cltManager = cltManager;
    }

    public void addChat(Long chatId) {
        chatManager.createChat(chatId);
    }

    public void removeChat(Long chatId) {
        chatManager.deleteChat(chatId);
    }

    public @NotNull ListSourcesResponse getChatLinks(Long chatId, String tag) {
        var chat = chatManager.getChat(chatId);
        if (chat.isEmpty()) throw new ChatNotFoundException();
        var links = chat.get().getLinks();
        var res = new ArrayList<LinkDto>();
        for (var l : links) {
            var lTags = cltManager.getChatLinkTags(chatId, l.getUrl()).stream()
                    .map(TagEntity::getName)
                    .toList();
            if (lTags.contains(tag)) {
                res.add(new LinkDto(l.getUrl(), lTags));
            }
        }
        return new ListSourcesResponse(res, res.size());
    }

    public @NotNull LinkDto addLink(Long chatId, AddSourceRequest req) {
        var chat = chatManager.getChat(chatId);
        if (chat.isEmpty()) throw new ChatNotFoundException();
        clManager.addLinkToChat(chatId, req.url());
        for (var t : req.tags()) {
            cltManager.addTagToChatLink(chatId, req.url(), t);
        }
        return new LinkDto(req.url(), req.tags());
    }

    public void removeLink(Long chatId, RemoveSourceRequest req) {
        var chat = chatManager.getChat(chatId);
        if (chat.isEmpty()) throw new ChatNotFoundException();
        clManager.removeLink(chatId, req.url());
    }
}
