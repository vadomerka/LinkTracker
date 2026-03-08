package backend.academy.linktracker.scrapper.services.requests;

import backend.academy.linktracker.scrapper.models.LinkUpdateRequest;
import backend.academy.linktracker.scrapper.properties.TelegramProperties;
import backend.academy.linktracker.services.RequestsUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class BotRequestsSender {
    private final RestClient restClient;

    public BotRequestsSender(TelegramProperties properties) {
        this.restClient = RestClient.create(properties.getTgUrl());
    }

    public ResponseEntity<String> sendUpdates(LinkUpdateRequest request) {
        return restClient.method(HttpMethod.POST)
            .uri("/link-updates")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .onStatus(HttpStatusCode::isError, RequestsUtils::onScrapperErrors)
            .toEntity(String.class);
    }
}
