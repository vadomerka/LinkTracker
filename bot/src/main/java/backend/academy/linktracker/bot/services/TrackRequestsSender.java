package backend.academy.linktracker.bot.services;

import backend.academy.linktracker.bot.models.exceptions.ScrapperRequestException;
import backend.academy.linktracker.bot.properties.TelegramProperties;
import backend.academy.linktracker.models.AddSourceRequest;
import backend.academy.linktracker.models.ApiErrorResponse;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class TrackRequestsSender {
    private final RestClient restClient;

    public TrackRequestsSender(TelegramProperties properties) {
        this.restClient = RestClient.create(properties.getScrapperUrl());
    }

    private void onScrapperErrors(HttpRequest req, ClientHttpResponse res) throws IOException {
        String body = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
        ApiErrorResponse errorBody = new ObjectMapper().readValue(body, ApiErrorResponse.class);
        throw new ScrapperRequestException(errorBody.description());
    }

    public ResponseEntity<String> addChat(Long id) {
        return restClient.post()
            .uri(String.format("/tg-chat/%d", id))
            .contentType(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(r -> !r.is2xxSuccessful(), (this::onScrapperErrors))
            .toEntity(String.class);
    }

    public ResponseEntity<String> addTrackingUrl(Long chatId, String url, List<String> tags, List<String> filters) {


        return restClient.post()
            .uri("/links")
            .contentType(MediaType.APPLICATION_JSON)
            .header("tgChatId", String.valueOf(chatId))
            .body(new AddSourceRequest(url, tags, filters))
            .retrieve()
            .onStatus(r -> !r.is2xxSuccessful(), (this::onScrapperErrors))
            .toEntity(String.class);
    }
}
