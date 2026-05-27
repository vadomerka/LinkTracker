package backend.academy.linktracker.bot.services.requests;

import backend.academy.linktracker.bot.properties.TelegramProperties;
import backend.academy.linktracker.models.http.internal.AddSourceRequest;
import backend.academy.linktracker.models.http.internal.ListSourcesResponse;
import backend.academy.linktracker.models.http.internal.RemoveSourceRequest;
import backend.academy.linktracker.services.RequestsUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.List;

@Service
public class TrackedRequestsSender {
    private final RestClient restClient;

    public TrackedRequestsSender(TelegramProperties properties) {
        this.restClient = RestClient.create(properties.getScrapperUrl());
    }

    public ResponseEntity<ListSourcesResponse> getTrackingUrls(Long chatId, String tag) {
        return restClient
                .method(HttpMethod.GET)
                .uri("/links")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Tg-Chat-Id", String.valueOf(chatId))
                .header("tag", tag)
                .retrieve()
                .onStatus(HttpStatusCode::isError, RequestsUtils::onScrapperErrors)
                .toEntity(ListSourcesResponse.class);
    }

    public void addTrackingUrl(Long chatId, String url, List<String> tags, List<String> filters) {
        restClient
                .post()
                .uri("/links")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Tg-Chat-Id", String.valueOf(chatId))
                .body(new AddSourceRequest(url, tags, filters))
                .retrieve()
                .onStatus(HttpStatusCode::isError, RequestsUtils::onScrapperErrors)
                .toEntity(String.class);
    }

    public void removeTrackingUrl(Long chatId, String url) {
        restClient
                .method(HttpMethod.DELETE)
                .uri("/links")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Tg-Chat-Id", String.valueOf(chatId))
                .body(new RemoveSourceRequest(url))
                .retrieve()
                .onStatus(HttpStatusCode::isError, RequestsUtils::onScrapperErrors)
                .toEntity(String.class);
    }
}
