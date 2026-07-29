package backend.academy.linktracker.scrapper.services.managers;

import backend.academy.linktracker.scrapper.models.entities.ChatLinkTag;
import backend.academy.linktracker.scrapper.models.entities.TagEntity;
import java.util.List;

public interface ChatLinkTagManager {

    ChatLinkTag findChatLinkTag(Long chatId, String url, String tagName);

    List<TagEntity> getChatLinkTags(Long chatId, String url);

    ChatLinkTag addTagToChatLink(Long chatId, String url, String tagName);

    boolean removeLinkTag(Long chatId, String url, String tagName);
}
