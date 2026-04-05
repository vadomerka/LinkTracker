package backend.academy.linktracker.scrapper.services.updates;

import backend.academy.linktracker.models.http.internal.LinkUpdateRequest;
import backend.academy.linktracker.scrapper.models.entities.LinkEntity;
import backend.academy.linktracker.scrapper.services.managers.ChatManager;
import backend.academy.linktracker.scrapper.services.managers.LinkManager;
import backend.academy.linktracker.scrapper.services.managers.orm.OrmChatManager;
import backend.academy.linktracker.scrapper.services.managers.orm.OrmLinkManager;
import backend.academy.linktracker.scrapper.services.requests.BotRequestsSender;
import backend.academy.linktracker.scrapper.services.requests.ScrapperSenderService;
import java.util.HashSet;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LinkUpdateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LinkUpdateService.class);
    private final ChatManager chManager;
    private final LinkManager lManager;
    private final BotRequestsSender botSender;
    private final ScrapperSenderService senderService;
    private final LinkUpdateInfoAnalyzeService analyzeService;

    public LinkUpdateService(
            BotRequestsSender botSender,
            OrmChatManager chManager,
            OrmLinkManager lManager,
            ScrapperSenderService senderService,
            LinkUpdateInfoAnalyzeService analyzeService) {
        this.botSender = botSender;
        this.chManager = chManager;
        this.lManager = lManager;
        this.senderService = senderService;
        this.analyzeService = analyzeService;
    }

    public void updateLinks() {
        var activeLinks = lManager.getAllLinks();
        var updLinks = getUpdLinks(activeLinks);

        if (updLinks == null || updLinks.isEmpty()) {
            LOGGER.info("Обновлений не обнаружено");
            return;
        }

        for (var chat : chManager.getAllChats()) {
            var updChatLinks = chManager.getContained(chat.getChatId(), updLinks);
            if (updChatLinks.isEmpty()) continue;
            botSender.sendUpdates(
                    chat.getChatId(),
                    new LinkUpdateRequest(
                            updLinks.stream().map(LinkEntity::getUrl).toList()));
        }
    }

    private List<LinkEntity> getUpdLinks(List<LinkEntity> activeLinks) {
        if (activeLinks.isEmpty()) {
            LOGGER.info("Список ссылок пуст");
            return null;
        }
        var updLinks = new HashSet<LinkEntity>();
        LOGGER.info("activeLinks size: {}", activeLinks.size());
        for (var al : activeLinks) {
            var updInfo = senderService.getUrlUpdate(al.getUrl());
            var time = analyzeService.getUpdTime(updInfo);
            if (time == null) continue;
            LOGGER.info("url - {}; time - {}", al.getUrl(), time);
            if (lManager.isUpdated(al.getUrl(), time)) {
                updLinks.add(al);
            }
        }
        return updLinks.stream().toList();
    }
}
