package backend.academy.linktracker.scrapper.services;

import backend.academy.linktracker.models.exceptions.GitHubRequestException;
import backend.academy.linktracker.scrapper.services.requests.GitHubRequestSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.HashSet;

@Component
public class LinkUpdateScheduler {
    private final TrackedSourceManager tsManager;
    private final LinkUpdateManager luManager;
    private final GitHubRequestSender scrapper;
    private final LinkUpdateService updateService;

    public LinkUpdateScheduler(TrackedSourceManager tsManager, LinkUpdateManager luManager,
                               GitHubRequestSender scrapper, LinkUpdateService updateService) {
        this.tsManager = tsManager;
        this.luManager = luManager;
        this.scrapper = scrapper;
        this.updateService = updateService;
    }

    @Scheduled(fixedDelay = 3600000)
    public void checkForUpdates() {
        var activeLinks = tsManager.getUniqueLinks();
        var updLinks = new HashSet<String>();
        for (var al: activeLinks) {
            var res = scrapper.getResponse(al.url());
            if (res.getBody() == null) throw new GitHubRequestException("Null response");

            var time = Instant.parse(res.getBody().updatedAt());
            if (luManager.isUpdated(al.url(), time)) {
                System.out.println(time);
                updLinks.add(al.url());
            }
        }
        updateService.update(updLinks.stream().toList());
    }
}
