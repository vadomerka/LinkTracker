package backend.academy.linktracker.scrapper.services.managers;

import backend.academy.linktracker.scrapper.models.entities.LinkEntity;

public interface ChatLinkManager {

    LinkEntity findChatLink(Long chatId, String url);

    LinkEntity addLink(Long chatId, String url);

    boolean removeLink(Long chatId, String url);
}
