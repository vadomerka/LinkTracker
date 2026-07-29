package backend.academy.linktracker.scrapper.services.managers.orm;

import backend.academy.linktracker.scrapper.models.entities.ChatLink;
import backend.academy.linktracker.scrapper.models.entities.ChatLinkId;
import backend.academy.linktracker.scrapper.models.exceptions.ChatLinkNotFoundException;
import backend.academy.linktracker.scrapper.models.exceptions.ChatNotFoundException;
import backend.academy.linktracker.scrapper.models.exceptions.LinkNotFoundException;
import backend.academy.linktracker.scrapper.models.exceptions.SourceIsAlreadyTrackedException;
import backend.academy.linktracker.scrapper.repositories.ChatLinkRepository;
import backend.academy.linktracker.scrapper.repositories.ChatRepository;
import backend.academy.linktracker.scrapper.repositories.LinkRepository;
import backend.academy.linktracker.scrapper.services.managers.ChatLinkManager;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class OrmChatLinkManager implements ChatLinkManager {
    private final ChatRepository chRepository;
    private final LinkRepository lRepository;
    private final ChatLinkRepository clRepository;

    public OrmChatLinkManager(
            ChatRepository chRepository, LinkRepository lRepository, ChatLinkRepository clRepository) {
        this.chRepository = chRepository;
        this.lRepository = lRepository;
        this.clRepository = clRepository;
    }

    private ChatLinkId generateChatLinkId(Long chatId, String url) {
        chRepository.findById(chatId).orElseThrow(ChatNotFoundException::new);
        var link = lRepository.findById(url).orElseThrow(LinkNotFoundException::new);
        return new ChatLinkId(chatId, link.getUrl());
    }

    @Override
    public ChatLink findChatLink(Long chatId, String url) {
        var id = generateChatLinkId(chatId, url);
        return clRepository.findById(id).orElseThrow(ChatLinkNotFoundException::new);
    }

    @Override
    public ChatLink addLinkToChat(Long chatId, String url) {
        var chat = chRepository.findById(chatId).orElseThrow(ChatNotFoundException::new);
        var link = lRepository.findById(url).orElseThrow(LinkNotFoundException::new);
        var id = new ChatLinkId(chatId, link.getUrl());
        if (clRepository.findById(id).isPresent()) throw new SourceIsAlreadyTrackedException();
        return clRepository.save(new ChatLink(chat, link));
    }

    @Override
    @Transactional
    public boolean removeLink(Long chatId, String url) {
        if (!chRepository.existsById(chatId) || !lRepository.existsById(url)) {
            return false;
        }

        var id = new ChatLinkId(chatId, url);
        if (!clRepository.existsById(id)) return false;
        clRepository.deleteById(id);

        if (clRepository.getByLink(lRepository.getReferenceById(url)).isEmpty()) {
            lRepository.deleteById(url);
        }
        return true;
    }
}
