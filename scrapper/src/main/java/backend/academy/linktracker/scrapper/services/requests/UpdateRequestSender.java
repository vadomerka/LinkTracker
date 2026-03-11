package backend.academy.linktracker.scrapper.services.requests;

import java.time.Instant;

public interface UpdateRequestSender {
    String getRoot();

    Instant getResponse(String url);
}
