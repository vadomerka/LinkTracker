package backend.academy.linktracker.scrapper.services.managers.orm;

import backend.academy.linktracker.scrapper.models.entities.ChatLinkId;
import backend.academy.linktracker.scrapper.models.entities.ChatLinkTag;
import backend.academy.linktracker.scrapper.models.entities.ChatLinkTagId;
import backend.academy.linktracker.scrapper.models.entities.TagEntity;
import backend.academy.linktracker.scrapper.models.exceptions.ChatLinkNotFoundException;
import backend.academy.linktracker.scrapper.models.exceptions.ChatLinkTagNotFoundException;
import backend.academy.linktracker.scrapper.models.exceptions.ChatNotFoundException;
import backend.academy.linktracker.scrapper.models.exceptions.LinkNotFoundException;
import backend.academy.linktracker.scrapper.models.exceptions.TagNotFoundException;
import backend.academy.linktracker.scrapper.repositories.*;
import backend.academy.linktracker.scrapper.services.managers.ChatLinkTagManager;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OrmChatLinkTagManager implements ChatLinkTagManager {
    private final ChatRepository chRepository;
    private final LinkRepository lRepository;
    private final TagRepository tRepository;
    private final ChatLinkRepository clRepository;
    private final ChatLinkTagRepository cltRepository;

    public OrmChatLinkTagManager(
            ChatRepository chRepository,
            LinkRepository lRepository,
            TagRepository tRepository,
            ChatLinkRepository clRepository,
            ChatLinkTagRepository cltRepository) {
        this.chRepository = chRepository;
        this.lRepository = lRepository;
        this.tRepository = tRepository;
        this.clRepository = clRepository;
        this.cltRepository = cltRepository;
    }

    private ChatLinkId getChatLinkId(Long chatId, String url) {
        chRepository.findById(chatId).orElseThrow(ChatNotFoundException::new);
        lRepository.findById(url).orElseThrow(LinkNotFoundException::new);
        return new ChatLinkId(chatId, url);
    }

    @Override
    public ChatLinkTag findChatLinkTag(Long chatId, String url, String tagName) {
        clRepository
                .findById(getChatLinkId(chatId, url))
                .orElseThrow(ChatLinkNotFoundException::new);
        var id = new ChatLinkTagId(chatId, url, tagName);
        return cltRepository
                .findById(id)
                .orElseThrow(ChatLinkTagNotFoundException::new);
    }

    public List<TagEntity> getChatLinkTags(Long chatId, String url) {
        var cl = clRepository
                .findById(getChatLinkId(chatId, url))
                .orElseThrow(ChatLinkNotFoundException::new);
        return cl.getTags();
    }

    @Override
    public ChatLinkTag addTagToChatLink(Long chatId, String url, String tagName) {
        var tag = tRepository.findById(tagName).orElseThrow(TagNotFoundException::new);
        var cl = clRepository
                .findById(getChatLinkId(chatId, url))
                .orElseThrow(ChatLinkNotFoundException::new);
        var id = new ChatLinkTagId(chatId, url, tagName);
        return cltRepository.findById(id).orElseGet(() -> cltRepository.save(new ChatLinkTag(cl, tag)));
    }

    @Override
    @Transactional
    public boolean removeLinkTag(Long chatId, String url, String tagName) {
        var chat = chRepository.findById(chatId);
        var link = lRepository.findById(url);
        var tag = tRepository.findById(tagName);
        if (chat.isEmpty() || link.isEmpty() || tag.isEmpty()) return false;
        var id = new ChatLinkTagId(chatId, url, tagName);
        if (!cltRepository.existsById(id)) return false;
        cltRepository.deleteById(id);
        return true;
    }
}
