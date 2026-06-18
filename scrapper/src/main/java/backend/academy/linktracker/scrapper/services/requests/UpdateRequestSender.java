package backend.academy.linktracker.scrapper.services.requests;

import backend.academy.linktracker.models.http.external.LinkUpdateData;

public interface UpdateRequestSender {
    String getRoot();

    boolean checkLink(String url);

    LinkUpdateData getResponse(String url);
}
