package backend.academy.linktracker.scrapper.services.senders;

import backend.academy.linktracker.models.http.internal.LinkUpdateRequest;
import backend.academy.linktracker.scrapper.properties.TelegramProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public interface BotRequestsSender {
    void sendUpdates(LinkUpdateRequest request);
}
