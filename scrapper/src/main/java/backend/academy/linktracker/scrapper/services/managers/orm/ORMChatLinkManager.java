package backend.academy.linktracker.scrapper.services.managers.orm;

import backend.academy.linktracker.scrapper.models.entities.ChatLink;
import backend.academy.linktracker.scrapper.models.entities.ChatLinkId;
import backend.academy.linktracker.scrapper.models.entities.ChatLinkTag;
import backend.academy.linktracker.scrapper.models.entities.ChatLinkTagId;
import backend.academy.linktracker.scrapper.models.exceptions.ChatLinkNotFoundException;
import backend.academy.linktracker.scrapper.models.exceptions.ChatNotFoundException;
import backend.academy.linktracker.scrapper.models.exceptions.LinkNotFoundException;
import backend.academy.linktracker.scrapper.models.exceptions.TagNotFoundException;
import backend.academy.linktracker.scrapper.repositories.ChatLinkRepository;
import backend.academy.linktracker.scrapper.repositories.ChatLinkTagRepository;
import backend.academy.linktracker.scrapper.repositories.ChatRepository;
import backend.academy.linktracker.scrapper.repositories.LinkRepository;
import backend.academy.linktracker.scrapper.repositories.TagRepository;
import backend.academy.linktracker.scrapper.services.managers.ChatLinkManager;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ORMChatLinkManager implements ChatLinkManager {
    private final ChatRepository chRepository;
    private final LinkRepository lRepository;
    private final TagRepository tRepository;
    private final ChatLinkRepository clRepository;
    private final ChatLinkTagRepository cltRepository;

    public ORMChatLinkManager(
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
        var chat = chRepository.findById(chatId).orElseThrow(() -> new ChatNotFoundException("Чат не найден."));
        var link = lRepository.findById(url).orElseThrow(() -> new LinkNotFoundException("Ссылка не найдена."));
        return new ChatLinkId(chatId, link.getUrl());
    }

    @Override
    public ChatLink findChatLink(Long chatId, String url) {
        var id = getChatLinkId(chatId, url);
        return clRepository.findById(id).orElseThrow(() -> new ChatLinkNotFoundException("Ссылка не привязана к данному чату."));
    }

    @Override
    public ChatLink addLinkToChat(Long chatId, String url) {
        var chat = chRepository.findById(chatId).orElseThrow(() -> new ChatNotFoundException("Чат не найден."));
        var link = lRepository.findById(url).orElseThrow(() -> new LinkNotFoundException("Ссылка не найдена."));
        var id = new ChatLinkId(chatId, link.getUrl());
        return clRepository.findById(id).orElseGet(() -> clRepository.save(new ChatLink(chat, link)));
    }

    @Override
    public ChatLinkTag addTagToChatLink(Long chatId, String url, String tagName) {
        var tag = tRepository.findById(tagName).orElseThrow(() -> new TagNotFoundException("Ссылка не найдена."));
        var cl = clRepository.findById(getChatLinkId(chatId, url))
                .orElseThrow(() -> new ChatLinkNotFoundException("Ссылка не привязана к данному чату."));
        var id = new ChatLinkTagId(chatId, url, tagName);
        return cltRepository.findById(id).orElseGet(() -> cltRepository.save(new ChatLinkTag(cl, tag)));
    }

    @Override
    @Transactional
    public boolean removeLink(Long chatId, String url) {
        var chat = chRepository.findById(chatId);
        var link = lRepository.findById(url);
        if (chat.isEmpty() || link.isEmpty()) return false;
        var id = new ChatLinkId(chatId, link.get().getUrl());
        if (!clRepository.existsById(id)) return false;
        clRepository.deleteById(id);
        return true;
    }
}
