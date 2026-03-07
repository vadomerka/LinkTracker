package backend.academy.linktracker.bot.services;

import backend.academy.linktracker.bot.models.requests.ApiErrorResponse;
import backend.academy.linktracker.bot.properties.TelegramProperties;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class TrackRequestsSender {
    private TelegramProperties properties;

    public TrackRequestsSender(TelegramProperties properties) {
        this.properties = properties;
    }

    public ResponseEntity<String> addTrackingUrl(String url, List<String> tags, List<String> filters) {
        var restClient = RestClient.create(properties.getScrapperUrl());

        return restClient.post()
            .uri("/tg-chat/1")
            .contentType(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(r -> !r.is2xxSuccessful(),
                (req, res) -> {
                String body = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
                ApiErrorResponse errorBody = new ObjectMapper().readValue(body, ApiErrorResponse.class);
                System.out.println(errorBody);
            })
            .toEntity(String.class);
    }
}
