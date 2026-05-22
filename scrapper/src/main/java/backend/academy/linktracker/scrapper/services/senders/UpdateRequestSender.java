package backend.academy.linktracker.scrapper.services.senders;

import backend.academy.linktracker.models.http.external.LinkUpdateData;

public interface UpdateRequestSender {
    boolean checkLink(String url);

    LinkUpdateData getLinkResponse(String url);
}
