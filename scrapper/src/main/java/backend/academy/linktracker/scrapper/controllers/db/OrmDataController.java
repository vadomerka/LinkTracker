package backend.academy.linktracker.scrapper.controllers.db;

import backend.academy.linktracker.models.LinkDto;
import backend.academy.linktracker.models.http.internal.AddSourceRequest;
import backend.academy.linktracker.models.http.internal.ListSourcesResponse;
import backend.academy.linktracker.models.http.internal.RemoveSourceRequest;
import backend.academy.linktracker.scrapper.models.entities.TagEntity;
import backend.academy.linktracker.scrapper.models.exceptions.*;
import backend.academy.linktracker.scrapper.services.managers.*;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

@Service
@Transactional
public class OrmDataController implements DataController {
    private final ChatManager chatManager;
    private final LinkManager linkManager;
    private final TagManager tagManager;
    private final ChatLinkManager clManager;
    private final ChatLinkTagManager cltManager;

    public OrmDataController(
            ChatManager chatManager,
            LinkManager linkManager,
            TagManager tagManager,
            ChatLinkManager clManager,
            ChatLinkTagManager cltManager) {
        this.chatManager = chatManager;
        this.linkManager = linkManager;
        this.tagManager = tagManager;
        this.clManager = clManager;
        this.cltManager = cltManager;
    }

    public void addChat(Long chatId) {
        if (chatManager.getChat(chatId).isPresent()) {
            throw new ChatAlreadyExistsException();
        }
        chatManager.createChat(chatId);
    }

    public void removeChat(Long chatId) {
        if (chatManager.getChat(chatId).isEmpty()) {
            throw new ChatNotFoundException();
        }
        chatManager.deleteChat(chatId);
    }

    public void addLink(String url) {
        if (linkManager.getLink(url).isPresent()) {
            throw new LinkAlreadyExistsException();
        }
        linkManager.createLink(url);
    }

    public void removeLink(String url) {
        if (linkManager.getLink(url).isEmpty()) {
            throw new LinkNotFoundException();
        }
        linkManager.deleteLink(url);
    }

    public @NotNull ListSourcesResponse getChatLinks(Long chatId, String tag) {
        var chat = chatManager.getChat(chatId).orElseThrow(ChatNotFoundException::new);
        var links = chat.getLinks();
        var res = new ArrayList<LinkDto>();
        for (var l : links) {
            var lTags = cltManager.getChatLinkTags(chatId, l.getUrl()).stream()
                    .map(TagEntity::getName)
                    .toList();
            if (tag.isEmpty() || lTags.contains(tag)) {
                res.add(new LinkDto(l.getUrl(), lTags));
            }
        }
        return new ListSourcesResponse(res, res.size());
    }

    public @NotNull LinkDto addLink(Long chatId, AddSourceRequest req) {
        chatManager.getChat(chatId).orElseThrow(ChatNotFoundException::new);
        if (linkManager.getLink(req.url()).isEmpty()) {
            linkManager.createLink(req.url());
        }

        clManager.addLinkToChat(chatId, req.url());
        for (var t : req.tags()) {
            if (tagManager.getTag(t).isEmpty() && !t.isEmpty()) {
                tagManager.createTag(t);
            }
            cltManager.addTagToChatLink(chatId, req.url(), t);
        }
        return new LinkDto(req.url(), req.tags());
    }

    public void removeLink(Long chatId, RemoveSourceRequest req) {
        var chat = chatManager.getChat(chatId);
        if (chat.isEmpty()) throw new ChatNotFoundException();
        var link = linkManager.getLink(req.url());
        if (link.isEmpty()) throw new LinkNotFoundException();
        if (!clManager.removeLink(chatId, req.url())) {
            throw new LinkRemovalException();
        }
    }
}
