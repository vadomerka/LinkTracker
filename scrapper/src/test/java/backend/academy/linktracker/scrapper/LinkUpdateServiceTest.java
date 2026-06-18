package backend.academy.linktracker.scrapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.linktracker.models.TrackedSource;
import backend.academy.linktracker.models.http.internal.LinkUpdateRequest;
import backend.academy.linktracker.scrapper.repositories.LinkUpdateRepository;
import backend.academy.linktracker.scrapper.repositories.TrackedSourceRepository;
import backend.academy.linktracker.scrapper.services.ScrapperSenderService;
import backend.academy.linktracker.scrapper.services.TrackedSourceManager;
import backend.academy.linktracker.scrapper.services.requests.BotRequestsSender;
import backend.academy.linktracker.scrapper.services.updates.LinkUpdateManager;
import backend.academy.linktracker.scrapper.services.updates.LinkUpdateService;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LinkUpdateServiceTest {

    private static final String GITHUB_URL = "https://api.github.com/repos/user/repo";
    private static final TrackedSource SOURCE =
            new TrackedSource(1, GITHUB_URL, List.of(), List.of());
    @Mock BotRequestsSender botSender;
    @Mock ScrapperSenderService senderService;
    private LinkUpdateService linkUpdateService;
    private TrackedSourceRepository repository;

    @BeforeEach
    void setUp() {
        repository = new TrackedSourceRepository();
        var linkUpdateRepository = new LinkUpdateRepository();
        var tsManager = new TrackedSourceManager(repository, new backend.academy.linktracker.scrapper.factories.TrackedSourceFactory());
        var luManager = new LinkUpdateManager(linkUpdateRepository);
        linkUpdateService = new LinkUpdateService(botSender, tsManager, luManager, senderService);
    }

    @Test
    void updateLinks_notifiesSubscribedChats() {
        repository.addChat(1L);
        repository.addChat(2L);
        repository.addLink(1L, SOURCE);

        Instant now = Instant.now();
        when(senderService.getUrlUpdate(GITHUB_URL)).thenReturn(now);

        linkUpdateService.updateLinks();

        verify(botSender, times(1)).sendUpdates(eq(1L), any(LinkUpdateRequest.class));
        verify(botSender, never()).sendUpdates(eq(2L), any(LinkUpdateRequest.class));
    }

    @Test
    void updateLinks_doesNotNotifyWhenNoUpdates() {
        repository.addChat(1L);
        repository.addLink(1L, SOURCE);

        when(senderService.getUrlUpdate(GITHUB_URL)).thenReturn(null);

        linkUpdateService.updateLinks();

        verify(botSender, never()).sendUpdates(anyLong(), any(LinkUpdateRequest.class));
    }

    @Test
    void updateLinks_doesNotNotifyWhenNoLinks() {
        repository.addChat(1L);

        linkUpdateService.updateLinks();

        verify(botSender, never()).sendUpdates(anyLong(), any(LinkUpdateRequest.class));
    }
}
