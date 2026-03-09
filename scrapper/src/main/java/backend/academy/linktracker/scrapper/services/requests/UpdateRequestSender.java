package backend.academy.linktracker.scrapper.services.requests;

import java.time.Instant;

public interface UpdateRequestSender {
    public String getRoot();
    public Instant getResponse(String url);
}
