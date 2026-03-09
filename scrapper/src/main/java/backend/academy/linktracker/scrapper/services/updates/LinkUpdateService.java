package backend.academy.linktracker.scrapper.services.updates;

import backend.academy.linktracker.models.GitHubResponse;
import backend.academy.linktracker.models.LinkUpdateRequest;
import backend.academy.linktracker.models.TrackedSource;
import backend.academy.linktracker.models.exceptions.GitHubRequestException;
import backend.academy.linktracker.models.exceptions.ScrapperRequestException;
import backend.academy.linktracker.scrapper.services.TrackedSourceManager;
import backend.academy.linktracker.scrapper.services.requests.BotRequestsSender;
import backend.academy.linktracker.scrapper.services.requests.GitHubRequestSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;

@Service
public class LinkUpdateService {
    private static final Logger logger = LoggerFactory.getLogger(LinkUpdateService.class);
    private final TrackedSourceManager tsManager;
    private final LinkUpdateManager luManager;
    private final BotRequestsSender botSender;
    private final GitHubRequestSender gitHubSender;

    public LinkUpdateService(BotRequestsSender botSender,
                             TrackedSourceManager tsManager, LinkUpdateManager luManager,
                             GitHubRequestSender gitHubSender) {
        this.botSender = botSender;
        this.tsManager = tsManager;
        this.luManager = luManager;
        this.gitHubSender = gitHubSender;
    }

    public void updateLinks() {
        var activeLinks = tsManager.getUniqueLinks();
        var updLinks = getUpdLinks(activeLinks);

        if (updLinks == null || updLinks.isEmpty()) {
            logger.info("Обновлений не обнаружено");
            return;
        }

        for (var chatId: tsManager.getUniqueChats()) {
            var updChatLinks = tsManager.filterChatLinks(chatId, updLinks);
            if (updChatLinks.isEmpty()) continue;
            botSender.sendUpdates(chatId, new LinkUpdateRequest(updLinks));
        }
    }

    private List<String> getUpdLinks(List<TrackedSource> activeLinks) {
        if (activeLinks.isEmpty()) {
            logger.info("Список ссылок пуст");
            return null;
        }
        var updLinks = new HashSet<String>();
        logger.info(String.valueOf(activeLinks.size()));
        for (var al: activeLinks) {
            ResponseEntity<GitHubResponse> res;
            try {
                res = gitHubSender.getResponse(al.url());
            } catch (ScrapperRequestException ex) {
                logger.info("Произошла ошибка при получении обновления по ссылке.");
                continue;
            } catch (Exception ex) {
                logger.info("Ссылка не соответствует формату.");
                continue;
            }

            if (res.getBody() == null) throw new GitHubRequestException("Null response");

            var time = Instant.parse(res.getBody().updatedAt());
            logger.info("url - {}; time - {}, response - {}", al.url(), time, res);
            if (luManager.isUpdated(al.url(), time)) {
                updLinks.add(al.url());
            }
        }
        return updLinks.stream().toList();
    }
}
