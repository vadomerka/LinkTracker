package backend.academy.linktracker.scrapper.services.cache;

import backend.academy.linktracker.models.http.internal.ListSourcesResponse;
import backend.academy.linktracker.scrapper.controllers.db.DataController;
import org.springframework.stereotype.Service;

@Service
public class CachedListSourcesService {
    private final DataController dataController;
    private final ListSourcesCacheService cacheService;

    public CachedListSourcesService(DataController dataController, ListSourcesCacheService cacheService) {
        this.dataController = dataController;
        this.cacheService = cacheService;
    }

    public ListSourcesResponse getChatLinks(Long tgChatId, String tag) {
        if (tag != null && !tag.isBlank()) {
            return dataController.getChatLinks(tgChatId, tag);
        }
        return cacheService.get(tgChatId).orElseGet(() -> {
            ListSourcesResponse response = dataController.getChatLinks(tgChatId, tag);
            cacheService.put(tgChatId, response);
            return response;
        });
    }

    public void remove(Long tgChatId) {
        cacheService.remove(tgChatId);
    }
}
