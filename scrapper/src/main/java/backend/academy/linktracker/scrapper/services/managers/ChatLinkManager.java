package backend.academy.linktracker.scrapper.services.managers;

import backend.academy.linktracker.scrapper.models.entities.ChatLink;
import backend.academy.linktracker.scrapper.models.entities.ChatLinkTag;

public interface ChatLinkManager {

    ChatLink findChatLink(Long chatId, String url);

    ChatLink addLinkToChat(Long chatId, String url);

    ChatLinkTag addTagToChatLink(Long chatId, String url, String tagName);

    boolean removeLink(Long chatId, String url);
}
