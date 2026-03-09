package backend.academy.linktracker.scrapper.services.requests;

import backend.academy.linktracker.models.LinkUpdateRequest;
import backend.academy.linktracker.scrapper.properties.TelegramProperties;
import backend.academy.linktracker.services.RequestsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class BotRequestsSender {
    private static final Logger logger = LoggerFactory.getLogger(BotRequestsSender.class);
    private final RestClient restClient;

    public BotRequestsSender(TelegramProperties properties) {
        this.restClient = RestClient.create(properties.getTgUrl());
    }

    public void sendUpdates(Long chatId, LinkUpdateRequest request) {
        var res = restClient.method(HttpMethod.POST)
            .uri(String.format("/tg-chat/%d", chatId))
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .onStatus(HttpStatusCode::isError, RequestsUtils::onScrapperErrors)
            .toEntity(String.class);
        logger.info(res.toString());
    }
}
