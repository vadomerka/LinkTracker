package backend.academy.linktracker.scrapper.services.updates;

import backend.academy.linktracker.models.http.internal.LinkUpdateRequest;
import backend.academy.linktracker.models.http.internal.LinkUpdateRequestItem;
import backend.academy.linktracker.scrapper.models.entities.LinkEntity;
import backend.academy.linktracker.scrapper.services.managers.ChatManager;
import backend.academy.linktracker.scrapper.services.managers.LinkManager;
import backend.academy.linktracker.scrapper.services.managers.orm.OrmChatManager;
import backend.academy.linktracker.scrapper.services.managers.orm.OrmLinkManager;
import backend.academy.linktracker.scrapper.services.senders.BotRequestsSender;
import backend.academy.linktracker.scrapper.services.senders.ScrapperSenderService;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LinkUpdateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LinkUpdateService.class);
    private final ChatManager chManager;
    private final LinkManager lManager;
    private final BotRequestsSender botRequestSender;
    private final ScrapperSenderService senderService;
    private final LinkUpdateInfoAnalyzeService analyzeService;

    public LinkUpdateService(
            BotRequestsSender botRequestSender,
            OrmChatManager chManager,
            OrmLinkManager lManager,
            ScrapperSenderService senderService,
            LinkUpdateInfoAnalyzeService analyzeService) {
        this.botRequestSender = botRequestSender;
        this.chManager = chManager;
        this.lManager = lManager;
        this.senderService = senderService;
        this.analyzeService = analyzeService;
    }

    public void updateLinks() {
        var activeLinks = lManager.getAllLinks().stream()
                .filter(le -> lManager.isActive(le.getUrl()))
                .toList();

        var updLinks = getUpdLinks(activeLinks);
        if (updLinks == null || updLinks.isEmpty()) {
            LOGGER.info("Обновлений не обнаружено");
            return;
        }

        sendUpdLinksToChats(updLinks);
    }

    private List<LinkUpdateRequestItem> getUpdLinks(List<LinkEntity> activeLinks) {
        if (activeLinks.isEmpty()) {
            LOGGER.info("Список ссылок пуст");
            return null;
        }
        LOGGER.info("activeLinks size: {}", activeLinks.size());

        var updLinks = new ArrayList<LinkUpdateRequestItem>();
        for (var al : activeLinks) {
            var apiData = senderService.getLinkUpdateData(al.getUrl());
            if (apiData == null) continue;

            var updData = analyzeService.getUpdTime(al.getUrl(), al.getLastUpdate(), apiData);
            if (updData == null) continue;

            LOGGER.info("url - {}; time - {}", al.getUrl(), updData.lastUpdate());
            if (lManager.isUpdated(al.getUrl(), updData.lastUpdate())) {
                updLinks.add(updData);
            }
        }
        return updLinks;
    }

    public void sendUpdLinksToChats(List<LinkUpdateRequestItem> updLinks) {
        for (var chat : chManager.getAllChats()) {
            var chatUrls = chManager.getLinks(chat.getChatId()).stream()
                    .map(LinkEntity::getUrl)
                    .toList();
            var chatUpdLinks =
                    updLinks.stream().filter(ul -> chatUrls.contains(ul.url())).toList();
            if (chatUpdLinks.isEmpty()) continue;

            botRequestSender.sendUpdates(new LinkUpdateRequest(chat.getChatId(), chatUpdLinks));
        }
    }

    public void testSend(List<LinkUpdateRequestItem> updLinks) {
        botRequestSender.sendUpdates(new LinkUpdateRequest(1L, updLinks));
    }
}
