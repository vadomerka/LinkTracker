package backend.academy.linktracker.scrapper.services.requests;

import backend.academy.linktracker.scrapper.models.updates.UpdateInfo;

public interface UpdateRequestSender {
    String getRoot();

    UpdateInfo getResponse(String url);
}
