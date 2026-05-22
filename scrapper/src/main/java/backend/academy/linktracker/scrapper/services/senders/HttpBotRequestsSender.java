package backend.academy.linktracker.scrapper.services.senders;

import backend.academy.linktracker.models.http.internal.LinkUpdateRequest;
import backend.academy.linktracker.scrapper.properties.TelegramProperties;
import backend.academy.linktracker.services.RequestsUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class HttpBotRequestsSender implements BotRequestsSender {
    protected final RestClient restClient;

    public HttpBotRequestsSender(TelegramProperties properties) {
        this.restClient = RestClient.create(properties.getTgUrl());
    }

    public void sendUpdates(LinkUpdateRequest request) {
        System.out.println("http send");
        restClient
                .method(HttpMethod.POST)
                .uri(String.format("/tg-chat/%d", request.chatId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, RequestsUtils::onScrapperErrors)
                .toEntity(String.class);
    }
}
