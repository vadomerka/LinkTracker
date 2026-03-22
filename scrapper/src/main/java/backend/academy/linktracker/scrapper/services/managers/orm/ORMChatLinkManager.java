package backend.academy.linktracker.scrapper.services.managers.orm;

import backend.academy.linktracker.scrapper.models.entities.LinkEntity;
import backend.academy.linktracker.scrapper.repositories.ChatLinkTagRepository;
import backend.academy.linktracker.scrapper.services.managers.ChatLinkManager;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;

@Service
public class ORMChatLinkManager implements ChatLinkManager {
    private ChatLinkTagRepository repository;

    public ORMChatLinkManager(ChatLinkTagRepository repository) {
        this.repository = repository;
    }

    @Override
    public LinkEntity findChatLink(Long chatId, String url) {
        return repository.findAllByIdChatIdAndIdLinkUrl(chatId, url).getFirst().getLink();
    }

    @Override
    public LinkEntity addLink(Long chatId, String url) {
        if (repository.existsByIdChatIdAndIdLinkUrl(chatId, url)) {
            return null;
        }

        throw new NotImplementedException("");
    }


    @Override
    @Transactional
    public boolean removeLink(Long chatId, String url) {
        if (!repository.existsByIdChatIdAndIdLinkUrl(chatId, url)) {
            return false;
        }
        repository.deleteByIdChatIdAndIdLinkUrl(chatId, url);
        return true;
    }
}
