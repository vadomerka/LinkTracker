package backend.academy.linktracker.scrapper.services;

import backend.academy.linktracker.scrapper.factories.LinkUpdateRequestFactory;
import backend.academy.linktracker.scrapper.services.requests.BotRequestsSender;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LinkUpdateService {
    private final LinkUpdateRequestFactory factory;
    private final BotRequestsSender sender;

    public LinkUpdateService(LinkUpdateRequestFactory factory, BotRequestsSender sender) {
        this.factory = factory;
        this.sender = sender;
    }

    public void update(List<String> links) {
        var req = factory.create(links);
        sender.sendUpdates(req);
    }
}
