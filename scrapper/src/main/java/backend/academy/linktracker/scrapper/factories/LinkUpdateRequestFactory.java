package backend.academy.linktracker.scrapper.factories;

import backend.academy.linktracker.models.http.internal.LinkUpdateRequest;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LinkUpdateRequestFactory {
    // Заглушка.
    public LinkUpdateRequest create(List<String> links) {
        return new LinkUpdateRequest(links);
    }
}
