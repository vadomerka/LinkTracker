package backend.academy.linktracker.scrapper.services.senders;

import backend.academy.linktracker.models.http.internal.LinkUpdateRequest;
import org.springframework.stereotype.Service;

@Service
public interface BotRequestsSender {
    void sendUpdates(LinkUpdateRequest request);
}
