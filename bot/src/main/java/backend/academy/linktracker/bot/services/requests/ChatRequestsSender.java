package backend.academy.linktracker.bot.services.requests;

import backend.academy.linktracker.bot.properties.TelegramProperties;
import backend.academy.linktracker.services.RequestsUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ChatRequestsSender {
    private final RestClient restClient;

    public ChatRequestsSender(TelegramProperties properties) {
        this.restClient = RestClient.create(properties.getScrapperUrl());
    }

    public ResponseEntity<String> addChat(Long chatId) {
        return restClient
                .post()
                .uri(String.format("/tg-chat/%d", chatId))
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, RequestsUtils::onScrapperErrors)
                .toEntity(String.class);
    }

    public ResponseEntity<String> removeChat(Long chatId) {
        return restClient
                .method(HttpMethod.DELETE)
                .uri(String.format("/tg-chat/%d", chatId))
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, RequestsUtils::onScrapperErrors)
                .toEntity(String.class);
    }
}
