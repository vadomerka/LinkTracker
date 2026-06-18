package backend.academy.linktracker.scrapper.services.updates;

import backend.academy.linktracker.models.TrackedSource;
import backend.academy.linktracker.models.http.internal.LinkUpdateRequest;
import backend.academy.linktracker.scrapper.services.ScrapperSenderService;
import backend.academy.linktracker.scrapper.services.TrackedSourceManager;
import backend.academy.linktracker.scrapper.services.requests.BotRequestsSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;

@Service
public class LinkUpdateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LinkUpdateService.class);
    private final TrackedSourceManager tsManager;
    private final LinkUpdateManager luManager;
    private final BotRequestsSender botSender;
    private final ScrapperSenderService senderService;

    public LinkUpdateService(
            BotRequestsSender botSender,
            TrackedSourceManager tsManager,
            LinkUpdateManager luManager,
            ScrapperSenderService senderService) {
        this.botSender = botSender;
        this.tsManager = tsManager;
        this.luManager = luManager;
        this.senderService = senderService;
    }

    public void updateLinks() {
        var activeLinks = tsManager.getUniqueLinks();
        var updLinks = getUpdLinks(activeLinks);

        if (updLinks == null || updLinks.isEmpty()) {
            LOGGER.info("Обновлений не обнаружено");
            return;
        }

        for (var chatId : tsManager.getUniqueChats()) {
            var updChatLinks = tsManager.filterChatLinks(chatId, updLinks);
            if (updChatLinks.isEmpty()) continue;
            botSender.sendUpdates(chatId, new LinkUpdateRequest(updLinks));
        }
    }

    private List<String> getUpdLinks(List<TrackedSource> activeLinks) {
        if (activeLinks.isEmpty()) {
            LOGGER.info("Список ссылок пуст");
            return null;
        }
        var updLinks = new HashSet<String>();
        LOGGER.info("activeLinks size: {}", activeLinks.size());
        for (var al : activeLinks) {
            var time = senderService.getUrlUpdate(al.url());
            if (time == null) continue;
            LOGGER.info("url - {}; time - {}", al.url(), time);
            if (luManager.isUpdated(al.url(), time)) {
                updLinks.add(al.url());
            }
        }
        return updLinks.stream().toList();
    }
}
