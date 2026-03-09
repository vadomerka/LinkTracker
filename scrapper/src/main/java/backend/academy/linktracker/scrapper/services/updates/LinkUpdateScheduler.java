package backend.academy.linktracker.scrapper.services.updates;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LinkUpdateScheduler {
    private final LinkUpdateService updateService;

    public LinkUpdateScheduler(LinkUpdateService updateService) {
        this.updateService = updateService;
    }

    @Scheduled(fixedDelay = 300000)
    public void checkForUpdates() {
        updateService.updateLinks();
    }
}
