package backend.academy.linktracker.bot.services.requests;

import backend.academy.linktracker.bot.models.exceptions.ScrapperRequestException;
import backend.academy.linktracker.bot.properties.TelegramProperties;
import backend.academy.linktracker.models.AddSourceRequest;
import backend.academy.linktracker.models.ApiErrorResponse;
import backend.academy.linktracker.models.RemoveSourceRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

@Service
public class ChatRequestsSender {
    private final RestClient restClient;
    private final RequestsUtils utils;

    public ChatRequestsSender(TelegramProperties properties, RequestsUtils utils) {
        this.restClient = RestClient.create(properties.getScrapperUrl());
        this.utils = utils;
    }

    public ResponseEntity<String> addChat(Long chatId) {
        return restClient.post()
            .uri(String.format("/tg-chat/%d", chatId))
            .contentType(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatusCode::isError, utils::onScrapperErrors)
            .toEntity(String.class);
    }

    public ResponseEntity<String> removeChat(Long chatId) {
        return restClient.method(HttpMethod.DELETE)
            .uri(String.format("/tg-chat/%d", chatId))
            .contentType(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatusCode::isError, utils::onScrapperErrors)
            .toEntity(String.class);
    }
}
