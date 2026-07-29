package backend.academy.linktracker.scrapper.services.updates;

import backend.academy.linktracker.models.http.external.LinkUpdateData;
import backend.academy.linktracker.models.http.external.UpdateResponse;
import backend.academy.linktracker.models.http.internal.LinkUpdateRequestItem;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LinkUpdateInfoAnalyzeService {
    public LinkUpdateRequestItem getUpdTime(String url, Instant lastUpd, LinkUpdateData updData) {
        List<UpdateResponse> newEvents = updData.data();
        if (lastUpd != null) {
            newEvents = updData.data().stream()
                    .filter(ur -> Instant.parse(ur.createdAt()).isAfter(lastUpd))
                    .toList();
        }
        if (newEvents.isEmpty()) return null;
        return new LinkUpdateRequestItem(
                url, Instant.parse(newEvents.getLast().createdAt()), new LinkUpdateData(newEvents));
    }
}
