package backend.academy.linktracker.bot.services.requests;

import backend.academy.linktracker.bot.properties.TelegramProperties;
import backend.academy.linktracker.models.AddSourceRequest;
import backend.academy.linktracker.models.RemoveSourceRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.List;

@Service
public class TrackedRequestsSender {
    private final RestClient restClient;
    private final RequestsUtils utils;

    public TrackedRequestsSender(TelegramProperties properties, RequestsUtils utils) {
        this.restClient = RestClient.create(properties.getScrapperUrl());
        this.utils = utils;
    }

    public void addTrackingUrl(Long chatId, String url, List<String> tags, List<String> filters) {
        restClient.post()
            .uri("/links")
            .contentType(MediaType.APPLICATION_JSON)
            .header("tgChatId", String.valueOf(chatId))
            .body(new AddSourceRequest(url, tags, filters))
            .retrieve()
            .onStatus(HttpStatusCode::isError, utils::onScrapperErrors)
            .toEntity(String.class);
    }

    public void removeTrackingUrl(Long chatId, String url) {
        restClient.method(HttpMethod.DELETE)
            .uri("/links")
            .contentType(MediaType.APPLICATION_JSON)
            .header("tgChatId", String.valueOf(chatId))
            .body(new RemoveSourceRequest(url))
            .retrieve()
            .onStatus(HttpStatusCode::isError, utils::onScrapperErrors)
            .toEntity(String.class);
    }
}
