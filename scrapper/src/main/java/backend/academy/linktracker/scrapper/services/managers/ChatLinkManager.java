package backend.academy.linktracker.scrapper.services.managers;

import backend.academy.linktracker.scrapper.models.entities.ChatLink;

public interface ChatLinkManager {

    ChatLink findChatLink(Long chatId, String url);

    ChatLink addLinkToChat(Long chatId, String url);

    boolean removeLink(Long chatId, String url);
}
